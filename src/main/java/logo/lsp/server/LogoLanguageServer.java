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
    public void connect(final LanguageClient client) {
        this.client = client;
        textDocumentService.setClient(client);
    }

    private static final SemanticTokensLegend SEMANTIC_TOKENS_LEGEND = new SemanticTokensLegend(
            List.of(
                    LspConstants.TOKEN_TYPE_KEYWORD,   // 0
                    LspConstants.TOKEN_TYPE_FUNCTION,  // 1
                    LspConstants.TOKEN_TYPE_VARIABLE,  // 2
                    LspConstants.TOKEN_TYPE_NUMBER,    // 3
                    LspConstants.TOKEN_TYPE_STRING     // 4
            ),
            List.of(LspConstants.TOKEN_MODIFIER_DECLARATION) // index 0 → bit 1
    );

    private static final SemanticTokensWithRegistrationOptions SEMANTIC_TOKEN_OPTS =
            buildSemanticTokenOpts();

    private static SemanticTokensWithRegistrationOptions buildSemanticTokenOpts() {
        final var opts = new SemanticTokensWithRegistrationOptions(SEMANTIC_TOKENS_LEGEND);
        opts.setFull(true);
        return opts;
    }

    private static final ServerInfo SERVER_INFO =
            new ServerInfo(LspConstants.SERVER_NAME, LspConstants.SERVER_VERSION);

    @Override
    public CompletableFuture<InitializeResult> initialize(final InitializeParams params) {
        final var caps = new ServerCapabilities();
        caps.setTextDocumentSync(TextDocumentSyncKind.Full);
        caps.setDefinitionProvider(true);
        caps.setHoverProvider(true);
        caps.setSemanticTokensProvider(SEMANTIC_TOKEN_OPTS);
        caps.setCompletionProvider(new CompletionOptions(false, List.of()));
        final var result = new InitializeResult(caps);
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
