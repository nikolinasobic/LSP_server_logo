package logo.lsp.server;

import logo.lsp.capabilities.LogoHoverDocs;
import logo.lsp.capabilities.SemanticTokenEncoder;
import logo.lsp.lexer.Token;
import logo.lsp.parser.LogoParser;
import logo.lsp.parser.ParseError;
import logo.lsp.parser.ParseResult;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.TextDocumentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class LogoTextDocumentService implements TextDocumentService {

    private static final String PROC_SIG_START = "```logo\nto ";
    private static final String PROC_SIG_END   = "\n...\nend\n```";
    private static final String PROC_PARAM_SEP = " :";
    private static final String HOVER_PROC_FMT = "**%s** *(user-defined procedure)*  \n%s";
    private static final String HOVER_VAR_FMT  = "**:%s** *(variable)*";

    // snippet insert texts for structural keywords (null = plain word insertion)
    private static final Map<String, String> SNIPPETS = Map.ofEntries(
        Map.entry("repeat",   "repeat ${1:count} [\n  $0\n]"),
        Map.entry("while",    "while ${1:condition} [\n  $0\n]"),
        Map.entry("until",    "until ${1:condition} [\n  $0\n]"),
        Map.entry("for",      "for [${1:var} ${2:start} ${3:end}] [\n  $0\n]"),
        Map.entry("dotimes",  "dotimes [${1:var} ${2:count}] [\n  $0\n]"),
        Map.entry("if",       "if ${1:condition} [\n  $0\n]"),
        Map.entry("ifelse",   "ifelse ${1:condition} [\n  $2\n] [\n  $0\n]"),
        Map.entry("to",       "to ${1:name}\n  $0\nend"),
        Map.entry("make",     "make \"${1:name} ${2:value}"),
        Map.entry("localmake","localmake \"${1:name} ${2:value}"),
        Map.entry("do.while", "do.while [\n  $0\n] ${1:condition}"),
        Map.entry("do.until", "do.until [\n  $0\n] ${1:condition}"),
        Map.entry("filled",   "filled ${1:color} [\n  $0\n]")
    );

    private record WordSpan(String text, int start, int end, boolean isVariable) {}

    private final DocumentStore store;
    private LanguageClient client;

    public LogoTextDocumentService(final DocumentStore store) {
        this.store = store;
    }

    public void setClient(final LanguageClient client) {
        this.client = client;
    }

    @Override
    public void didOpen(final DidOpenTextDocumentParams params) {
        final var uri    = params.getTextDocument().getUri();
        final var text   = params.getTextDocument().getText();
        final var result = store.update(uri, text);
        publishDiagnostics(uri, result);
    }

    @Override
    public void didChange(final DidChangeTextDocumentParams params) {
        final var uri     = params.getTextDocument().getUri();
        final var changes = params.getContentChanges();
        final var text    = changes.get(changes.size() - 1).getText();
        final var result  = store.update(uri, text);
        publishDiagnostics(uri, result);
    }

    @Override
    public void didClose(final DidCloseTextDocumentParams params) {
        final var uri = params.getTextDocument().getUri();
        store.remove(uri);
        if (client != null) {
            client.publishDiagnostics(new PublishDiagnosticsParams(uri, new ArrayList<>()));
        }
    }

    @Override
    public void didSave(final DidSaveTextDocumentParams params) {
        // re-parsed on every change
    }

    @Override
    public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> definition(
            final DefinitionParams params) {
        return resolveDefinition(
                params.getTextDocument().getUri(),
                params.getPosition());
    }

    private CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> resolveDefinition(
            final String uri, final Position pos) {

        final var state = store.getState(uri);
        if (state == null)
            return CompletableFuture.completedFuture(Either.forLeft(List.of()));

        final var result = state.result();
        final var span   = spanAt(state.lines(), pos.getLine(), pos.getCharacter());

        if (span == null || span.text().isEmpty())
            return CompletableFuture.completedFuture(Either.forLeft(List.of()));

        final var lower = span.text().toLowerCase();

        // variable reference
        if (span.isVariable() || result.variableDefinitions().containsKey(lower)) {
            final var varToken = result.variableDefinitions().get(lower);
            if (varToken != null) {
                return CompletableFuture.completedFuture(
                        Either.forLeft(List.of(tokenLocation(uri, varToken))));
            }
        }

        // procedure reference
        final var procToken = result.procedureDefinitions().get(lower);
        if (procToken != null) {
            return CompletableFuture.completedFuture(
                    Either.forLeft(List.of(tokenLocation(uri, procToken))));
        }

        return CompletableFuture.completedFuture(Either.forLeft(List.of()));
    }

    @Override
    public CompletableFuture<Hover> hover(final HoverParams params) {
        final var uri   = params.getTextDocument().getUri();
        final var state = store.getState(uri);
        final var pos   = params.getPosition();
        final var span  = spanAt(state != null ? state.lines() : null, pos.getLine(), pos.getCharacter());

        if (span == null || span.text().isEmpty())
            return CompletableFuture.completedFuture(null);

        final var lower = span.text().toLowerCase();
        final var range = new Range(new Position(pos.getLine(), span.start()),
                                    new Position(pos.getLine(), span.end()));

        // built-in command documentation
        final var doc = LogoHoverDocs.get(lower);
        if (doc != null) {
            final var hover = new Hover(new MarkupContent(LspConstants.MARKUP_MARKDOWN, doc));
            hover.setRange(range);
            return CompletableFuture.completedFuture(hover);
        }

        if (state != null) {
            final var result = state.result();
            // user-defined procedure
            final var procToken = result.procedureDefinitions().get(lower);
            if (procToken != null) {
                final var sig   = buildProcedureSignature(lower, result);
                final var hover = new Hover(new MarkupContent(LspConstants.MARKUP_MARKDOWN,
                        String.format(HOVER_PROC_FMT, lower, sig)));
                hover.setRange(range);
                return CompletableFuture.completedFuture(hover);
            }

            // variable
            if (span.isVariable() && result.variableDefinitions().containsKey(lower)) {
                final var hover = new Hover(new MarkupContent(LspConstants.MARKUP_MARKDOWN,
                        String.format(HOVER_VAR_FMT, lower)));
                hover.setRange(range);
                return CompletableFuture.completedFuture(hover);
            }
        }

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(
            final CompletionParams params) {
        final var uri   = params.getTextDocument().getUri();
        final var state = store.getState(uri);
        final var pos   = params.getPosition();
        final var span  = spanAt(state != null ? state.lines() : null,
                                 pos.getLine(), pos.getCharacter());

        final String  prefix = span != null ? span.text().toLowerCase() : "";
        final boolean isVar  = span != null && span.isVariable();

        final var items = new ArrayList<CompletionItem>();

        if (isVar) {
            if (state != null) {
                for (final String varName : state.result().variableDefinitions().keySet()) {
                    if (varName.startsWith(prefix)) {
                        final var item = new CompletionItem(varName);
                        item.setKind(CompletionItemKind.Variable);
                        items.add(item);
                    }
                }
            }
        } else {
            for (final String name : LogoParser.BUILTIN_NAMES) {
                if (!name.startsWith(prefix)) continue;
                final var item = new CompletionItem(name);
                item.setKind(CompletionItemKind.Keyword);
                final String snippet = SNIPPETS.get(name);
                if (snippet != null) {
                    item.setInsertText(snippet);
                    item.setInsertTextFormat(InsertTextFormat.Snippet);
                }
                final var doc = LogoHoverDocs.get(name);
                if (doc != null)
                    item.setDocumentation(new MarkupContent(LspConstants.MARKUP_MARKDOWN, doc));
                items.add(item);
            }
            if (state != null) {
                for (final String procName : state.result().procedureDefinitions().keySet()) {
                    if (procName.startsWith(prefix)) {
                        final var item = new CompletionItem(procName);
                        item.setKind(CompletionItemKind.Function);
                        final var sig = buildProcedureSignature(procName, state.result());
                        item.setDocumentation(new MarkupContent(LspConstants.MARKUP_MARKDOWN,
                                String.format(HOVER_PROC_FMT, procName, sig)));
                        items.add(item);
                    }
                }
            }
        }

        return CompletableFuture.completedFuture(Either.forLeft(items));
    }

    private String buildProcedureSignature(final String name, final ParseResult result) {
        final var params = result.procedureParams().get(name);
        final var sb = new StringBuilder(PROC_SIG_START).append(name);
        if (params != null) {
            for (final String param : params) sb.append(PROC_PARAM_SEP).append(param);
        }
        sb.append(PROC_SIG_END);
        return sb.toString();
    }

    private Location tokenLocation(final String uri, final Token token) {
        final var range = new Range(
                new Position(token.line(), token.startCol()),
                new Position(token.line(), token.endCol()));
        return new Location(uri, range);
    }

    @Override
    public CompletableFuture<SemanticTokens> semanticTokensFull(
            final SemanticTokensParams params) {
        final var uri   = params.getTextDocument().getUri();
        final var state = store.getState(uri);
        final var data  = state != null
                ? SemanticTokenEncoder.encode(state.result().tokens())
                : List.<Integer>of();
        return CompletableFuture.completedFuture(new SemanticTokens(data));
    }

    private WordSpan spanAt(final String[] lines, final int line, final int character) {
        if (lines == null) return null;
        if (line >= lines.length) return null;
        final String ln = lines[line];
        if (character > ln.length()) return null;
        int start = character;
        while (start > 0 && isIdentChar(ln.charAt(start - 1))) start--;
        int end = character;
        while (end < ln.length() && isIdentChar(ln.charAt(end))) end++;
        final boolean isVariable = start > 0 && ln.charAt(start - 1) == ':';
        return new WordSpan(ln.substring(start, end), start, end, isVariable);
    }

    private boolean isIdentChar(final char c) {
        return Character.isLetterOrDigit(c) || c == '_' || c == '?' || c == '!';
    }

    private void publishDiagnostics(final String uri, final ParseResult result) {
        if (client == null)
            return;

        final var diags = new ArrayList<Diagnostic>();
        for (final ParseError err : result.errors()) {
            final var d = new Diagnostic();
            d.setMessage(err.message);
            d.setSeverity(err.severity == ParseError.Severity.ERROR
                    ? DiagnosticSeverity.Error
                    : DiagnosticSeverity.Warning);
            d.setRange(new Range(
                    new Position(err.line, err.startCol),
                    new Position(err.line, err.endCol)));
            d.setSource(LspConstants.DIAGNOSTIC_SOURCE);
            diags.add(d);
        }
        client.publishDiagnostics(new PublishDiagnosticsParams(uri, diags));
    }
}
