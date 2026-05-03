package logo.lsp.server;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.*;

import java.util.List;
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

    private static final SemanticTokensLegend SEMANTIC_TOKENS_LEGEND = new SemanticTokensLegend(
            List.of(
                    "keyword",    // 0
                    "function",   // 1
                    "variable",   // 2
                    "number",     // 3
                    "string",     // 4
                    "comment"     // 5
            ),
            List.of() // no modifiers
    );

    private static final SemanticTokensWithRegistrationOptions SEMANTIC_TOKEN_OPTS =
            new SemanticTokensWithRegistrationOptions(SEMANTIC_TOKENS_LEGEND);

    static {
        SEMANTIC_TOKEN_OPTS.setFull(true);
    }

    private static final ServerInfo SERVER_INFO = new ServerInfo("Logo Language Server", "1.0.0");

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        ServerCapabilities caps = new ServerCapabilities();

        caps.setTextDocumentSync(TextDocumentSyncKind.Full);
        caps.setDefinitionProvider(true);
        caps.setHoverProvider(true);
        caps.setSemanticTokensProvider(SEMANTIC_TOKEN_OPTS);

        InitializeResult result = new InitializeResult(caps);
        result.setServerInfo(SERVER_INFO);
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
