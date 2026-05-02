package logo.lsp.server;

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
