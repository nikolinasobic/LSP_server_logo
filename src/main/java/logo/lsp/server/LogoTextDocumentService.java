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


public class LogoTextDocumentService implements TextDocumentService{
    private final DocumentStore store;
    private LanguageClient client;

    public LogoTextDocumentService(DocumentStore store){
        this.store = store;
    }

    public void setClient(LanguageClient client){
        this.client = client;
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams didOpenTextDocumentParams) {
        String uri = didOpenTextDocumentParams.getTextDocument().getUri();
        String text = didOpenTextDocumentParams.getTextDocument().getText();
        ParseResult result = store.update(uri, text);
        publishDiagnostics(uri, result);
    }

    @Override
    public void didChange(DidChangeTextDocumentParams didChangeTextDocumentParams) {
        String uri = didChangeTextDocumentParams.getTextDocument().getUri();
        String text = didChangeTextDocumentParams.getContentChanges().get(didChangeTextDocumentParams.getContentChanges().size()-1).getText();
        ParseResult result = store.update(uri, text);
        publishDiagnostics(uri, result);
    }

    @Override
    public void didClose(DidCloseTextDocumentParams didCloseTextDocumentParams) {
        String uri = didCloseTextDocumentParams.getTextDocument().getUri();
        store.remove(uri);

        if(client != null){
            client.publishDiagnostics(new PublishDiagnosticsParams(uri, new ArrayList<>()));
        }
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
        //we re-parse on every change already
    }

    @Override
    public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> definition(
            DefinitionParams params) {
        return resolveDefinition(
                params.getTextDocument().getUri(),
                params.getPosition());
    }


    private CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> resolveDefinition(
            String uri, Position pos) {

        ParseResult result = store.get(uri);
        if (result == null)
            return CompletableFuture.completedFuture(Either.forLeft(List.of()));

        String source = store.getSource(uri);

        boolean onVariable = cursorOnVariable(source, pos.getLine(), pos.getCharacter());
        String word = wordAt(source, pos.getLine(), pos.getCharacter());

        if (word == null || word.isEmpty())
            return CompletableFuture.completedFuture(Either.forLeft(List.of()));

        String lower = word.toLowerCase();

        // variable reference
        if (onVariable || result.variableDefinitions.containsKey(lower)) {
            var varToken = result.variableDefinitions.get(lower);
            if (varToken != null) {
                return CompletableFuture.completedFuture(
                        Either.forLeft(List.of(tokenLocation(uri, varToken))));
            }
        }

        // procedure reference
        var procToken = result.procedureDefinitions.get(lower);
        if (procToken != null) {
            return CompletableFuture.completedFuture(
                    Either.forLeft(List.of(tokenLocation(uri, procToken))));
        }

        return CompletableFuture.completedFuture(Either.forLeft(List.of()));
    }

    @Override
    public CompletableFuture<Hover> hover(HoverParams params) {
        String uri    = params.getTextDocument().getUri();
        String source = store.getSource(uri);
        ParseResult result = store.get(uri);

        Position pos = params.getPosition();
        String word = wordAt(source, pos.getLine(), pos.getCharacter());
        if (word == null || word.isEmpty())
            return CompletableFuture.completedFuture(null);

        String lower = word.toLowerCase();

        // built-in command documentation
        String doc = LogoHoverDocs.get(lower);
        if (doc != null) {
            Hover hover = new Hover(new MarkupContent("markdown", doc));
            hover.setRange(wordRange(source, pos.getLine(), pos.getCharacter()));
            return CompletableFuture.completedFuture(hover);
        }

        // user-defined procedure (show its signature)
        if (result != null) {
            var procToken = result.procedureDefinitions.get(lower);
            if (procToken != null) {
                String sig = buildProcedureSignature(lower, result);
                Hover hover = new Hover(new MarkupContent("markdown",
                        "**" + lower + "** *(user-defined procedure)*  \n" + sig));
                hover.setRange(wordRange(source, pos.getLine(), pos.getCharacter()));
                return CompletableFuture.completedFuture(hover);
            }

            // variable  (show its name and that it is a variable)
            boolean onVar = cursorOnVariable(source, pos.getLine(), pos.getCharacter());
            if (onVar && result.variableDefinitions.containsKey(lower)) {
                Hover hover = new Hover(new MarkupContent("markdown",
                        "**:" + lower + "** *(variable)*"));
                hover.setRange(wordRange(source, pos.getLine(), pos.getCharacter()));
                return CompletableFuture.completedFuture(hover);
            }
        }

        return CompletableFuture.completedFuture(null);
    }

    private String buildProcedureSignature(String name, ParseResult result) {
        // Walk the AST to find the ProcedureDef node for this name
        for (logo.lsp.ast.Node stmt : result.program.statements) {
            if (stmt instanceof logo.lsp.ast.Node.ProcedureDef def
                    && def.name.equals(name)) {
                StringBuilder sb = new StringBuilder("```logo\nto ").append(name);
                for (String param : def.params) sb.append(" :").append(param);
                sb.append("\n...\nend\n```");
                return sb.toString();
            }
        }
        return "```logo\nto " + name + "\n...\nend\n```";
    }

    private Range wordRange(String source, int line, int character) {
        String[] lines = source.split("\n", -1);
        if (line >= lines.length) return new Range(new Position(line, character), new Position(line, character));
        String ln = lines[line];
        int start = character;
        while (start > 0 && isIdentChar(ln.charAt(start - 1))) start--;
        int end = character;
        while (end < ln.length() && isIdentChar(ln.charAt(end))) end++;
        return new Range(new Position(line, start), new Position(line, end));
    }


    private Location tokenLocation(String uri, logo.lsp.lexer.Token token) {
        Range range = new Range(
                new Position(token.line, token.startCol),
                new Position(token.line, token.endCol));
        return new Location(uri, range);
    }

    private boolean cursorOnVariable(String source, int line, int character) {
        String[] lines = source.split("\n", -1);
        if (line >= lines.length) return false;
        String ln = lines[line];
        int start = character;
        while (start > 0 && isIdentChar(ln.charAt(start - 1))) start--;
        return start > 0 && ln.charAt(start - 1) == ':';
    }


    @Override
    public CompletableFuture<SemanticTokens> semanticTokensFull(
            SemanticTokensParams params) {
        String uri = params.getTextDocument().getUri();
        String source = store.getSource(uri);
        List<Integer> data = SemanticTokenEncoder.encode(source);
        return CompletableFuture.completedFuture(new SemanticTokens(data));
    }

    private String wordAt(String source, int line, int character) {
        String[] lines = source.split("\n", -1);
        if (line >= lines.length) return null;
        String ln = lines[line];
        if (character > ln.length()) return null;

        int start = character;
        while (start > 0 && isIdentChar(ln.charAt(start - 1))) start--;
        int end = character;
        while (end < ln.length() && isIdentChar(ln.charAt(end))) end++;

        return ln.substring(start, end);
    }

    private boolean isIdentChar(char c) {
        return Character.isLetterOrDigit(c) || c == '_' || c == '?' || c == '!';
    }

    private void publishDiagnostics(String uri, ParseResult result) {
        if (client == null)
            return;

        List<Diagnostic> diags = new ArrayList<>();
        for (ParseError err : result.errors) {
            Diagnostic d = new Diagnostic();
            d.setMessage(err.message);
            d.setSeverity(err.severity == ParseError.Severity.ERROR
                    ? DiagnosticSeverity.Error
                    : DiagnosticSeverity.Warning);
            d.setRange(new Range(
                    new Position(err.line, err.startCol),
                    new Position(err.line, err.endCol)));
            d.setSource("logo-lsp");
            diags.add(d);
        }
        client.publishDiagnostics(new PublishDiagnosticsParams(uri, diags));
    }

}
