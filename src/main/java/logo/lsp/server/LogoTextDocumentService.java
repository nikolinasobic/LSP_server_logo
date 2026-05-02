package logo.lsp.server;

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

        String uri = params.getTextDocument().getUri();
        ParseResult result = store.get(uri);
        if (result == null)
            return CompletableFuture.completedFuture(Either.forLeft(List.of()));

        Position pos = params.getPosition();
        String source = store.getSource(uri);

        String word = wordAt(source, pos.getLine(), pos.getCharacter());
        if (word == null || word.isEmpty())
            return CompletableFuture.completedFuture(Either.forLeft(List.of()));

        String lower = word.toLowerCase();

        var defToken = result.procedureDefinitions.get(lower);
        if (defToken != null) {
            Range range = new Range(
                    new Position(defToken.line, defToken.startCol),
                    new Position(defToken.line, defToken.endCol));

            Location loc = new Location(uri, range);

            return CompletableFuture.completedFuture(
                    Either.forLeft(List.of(loc))
            );
        }

        return CompletableFuture.completedFuture(Either.forLeft(List.of()));
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
