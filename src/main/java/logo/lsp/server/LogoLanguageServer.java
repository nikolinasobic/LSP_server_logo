package logo.lsp.server;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.*;

import java.util.concurrent.CompletableFuture;

public class LogoLanguageServer implements LanguageServer, LanguageClientAware {

    private final DocumentStore store = new DocumentStore();
    private final LogoTextDocumentService textDocumentService =
            new LogoTextDocumentService(store);
    private final LogoWorkspaceService workspaceService = new LogoWorkspaceService();

    private LanguageClient client;
    private int exitCode = 0;

    @Override
    public void connect(LanguageClient client) {
        this.client = client;
        textDocumentService.setClient(client);
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        ServerCapabilities caps = new ServerCapabilities();

        // full document sync
        caps.setTextDocumentSync(TextDocumentSyncKind.Full);

        // go to definition
        caps.setDefinitionProvider(true);
        caps.setHoverProvider(true);

        // syntax highlighting
        SemanticTokensLegend legend = new SemanticTokensLegend(
                java.util.List.of(
                        "keyword",    // 0
                        "function",   // 1
                        "variable",   // 2
                        "number",     // 3
                        "string",     // 4
                        "comment"     // 5
                ),
                java.util.List.of() // no modifiers
        );
        SemanticTokensWithRegistrationOptions semTokenOpts =
                new SemanticTokensWithRegistrationOptions(legend);
        semTokenOpts.setFull(true);
        caps.setSemanticTokensProvider(semTokenOpts);

        InitializeResult result = new InitializeResult(caps);
        ServerInfo info = new ServerInfo("Logo Language Server", "1.0.0");
        result.setServerInfo(info);
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        exitCode = 0;
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void exit() {
        System.exit(exitCode);
    }

    @Override
    public TextDocumentService getTextDocumentService() {
        return textDocumentService;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        return workspaceService;
    }

}
