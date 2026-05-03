package logo.lsp.server;

import logo.lsp.capabilities.LogoHoverDocs;
import logo.lsp.capabilities.SemanticTokenEncoder;
import logo.lsp.parser.ParseError;
import logo.lsp.parser.ParseResult;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.TextDocumentService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LogoTextDocumentService implements TextDocumentService {

    private static final String NEWLINE          = "\n";
    private static final String PROC_SIG_START   = "```logo\nto ";
    private static final String PROC_SIG_END     = "\n...\nend\n```";
    private static final String PROC_PARAM_SEP   = " :";
    private static final String HOVER_PROC_FMT   = "**%s** *(user-defined procedure)*  \n%s";
    private static final String HOVER_VAR_FMT    = "**:%s** *(variable)*";

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

        final var result = store.get(uri);
        if (result == null)
            return CompletableFuture.completedFuture(Either.forLeft(List.of()));

        final var source     = store.getSource(uri);
        final var onVariable = cursorOnVariable(source, pos.getLine(), pos.getCharacter());
        final var word       = wordAt(source, pos.getLine(), pos.getCharacter());

        if (word == null || word.isEmpty())
            return CompletableFuture.completedFuture(Either.forLeft(List.of()));

        final var lower = word.toLowerCase();

        // variable reference
        if (onVariable || result.variableDefinitions.containsKey(lower)) {
            final var varToken = result.variableDefinitions.get(lower);
            if (varToken != null) {
                return CompletableFuture.completedFuture(
                        Either.forLeft(List.of(tokenLocation(uri, varToken))));
            }
        }

        // procedure reference
        final var procToken = result.procedureDefinitions.get(lower);
        if (procToken != null) {
            return CompletableFuture.completedFuture(
                    Either.forLeft(List.of(tokenLocation(uri, procToken))));
        }

        return CompletableFuture.completedFuture(Either.forLeft(List.of()));
    }

    @Override
    public CompletableFuture<Hover> hover(final HoverParams params) {
        final var uri    = params.getTextDocument().getUri();
        final var source = store.getSource(uri);
        final var result = store.get(uri);
        final var pos    = params.getPosition();
        final var word   = wordAt(source, pos.getLine(), pos.getCharacter());

        if (word == null || word.isEmpty())
            return CompletableFuture.completedFuture(null);

        final var lower = word.toLowerCase();

        // built-in command documentation
        final var doc = LogoHoverDocs.get(lower);
        if (doc != null) {
            final var hover = new Hover(new MarkupContent(LspConstants.MARKUP_MARKDOWN, doc));
            hover.setRange(wordRange(source, pos.getLine(), pos.getCharacter()));
            return CompletableFuture.completedFuture(hover);
        }

        if (result != null) {
            // user-defined procedure
            final var procToken = result.procedureDefinitions.get(lower);
            if (procToken != null) {
                final var sig   = buildProcedureSignature(lower, result);
                final var hover = new Hover(new MarkupContent(LspConstants.MARKUP_MARKDOWN,
                        String.format(HOVER_PROC_FMT, lower, sig)));
                hover.setRange(wordRange(source, pos.getLine(), pos.getCharacter()));
                return CompletableFuture.completedFuture(hover);
            }

            // variable
            final var onVar = cursorOnVariable(source, pos.getLine(), pos.getCharacter());
            if (onVar && result.variableDefinitions.containsKey(lower)) {
                final var hover = new Hover(new MarkupContent(LspConstants.MARKUP_MARKDOWN,
                        String.format(HOVER_VAR_FMT, lower)));
                hover.setRange(wordRange(source, pos.getLine(), pos.getCharacter()));
                return CompletableFuture.completedFuture(hover);
            }
        }

        return CompletableFuture.completedFuture(null);
    }

    private String buildProcedureSignature(final String name, final ParseResult result) {
        for (final logo.lsp.ast.Node stmt : result.program.statements) {
            if (stmt instanceof logo.lsp.ast.Node.ProcedureDef def
                    && def.name.equals(name)) {
                final var sb = new StringBuilder(PROC_SIG_START).append(name);
                for (final String param : def.params) sb.append(PROC_PARAM_SEP).append(param);
                sb.append(PROC_SIG_END);
                return sb.toString();
            }
        }
        return PROC_SIG_START + name + PROC_SIG_END;
    }

    private Range wordRange(final String source, final int line, final int character) {
        final var lines = source.split(NEWLINE, -1);
        if (line >= lines.length)
            return new Range(new Position(line, character), new Position(line, character));
        final String ln = lines[line];
        int start = character;
        while (start > 0 && isIdentChar(ln.charAt(start - 1))) start--;
        int end = character;
        while (end < ln.length() && isIdentChar(ln.charAt(end))) end++;
        return new Range(new Position(line, start), new Position(line, end));
    }

    private Location tokenLocation(final String uri, final logo.lsp.lexer.Token token) {
        final var range = new Range(
                new Position(token.line, token.startCol),
                new Position(token.line, token.endCol));
        return new Location(uri, range);
    }

    private boolean cursorOnVariable(final String source, final int line, final int character) {
        final var lines = source.split(NEWLINE, -1);
        if (line >= lines.length) return false;
        final String ln = lines[line];
        int start = character;
        while (start > 0 && isIdentChar(ln.charAt(start - 1))) start--;
        return start > 0 && ln.charAt(start - 1) == ':';
    }

    @Override
    public CompletableFuture<SemanticTokens> semanticTokensFull(
            final SemanticTokensParams params) {
        final var uri    = params.getTextDocument().getUri();
        final var source = store.getSource(uri);
        final var data   = SemanticTokenEncoder.encode(source);
        return CompletableFuture.completedFuture(new SemanticTokens(data));
    }

    private String wordAt(final String source, final int line, final int character) {
        final var lines = source.split(NEWLINE, -1);
        if (line >= lines.length) return null;
        final String ln = lines[line];
        if (character > ln.length()) return null;

        int start = character;
        while (start > 0 && isIdentChar(ln.charAt(start - 1))) start--;
        int end = character;
        while (end < ln.length() && isIdentChar(ln.charAt(end))) end++;

        return ln.substring(start, end);
    }

    private boolean isIdentChar(final char c) {
        return Character.isLetterOrDigit(c) || c == '_' || c == '?' || c == '!';
    }

    private void publishDiagnostics(final String uri, final ParseResult result) {
        if (client == null)
            return;

        final var diags = new ArrayList<Diagnostic>();
        for (final ParseError err : result.errors) {
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
