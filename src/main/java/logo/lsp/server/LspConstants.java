package logo.lsp.server;

public final class LspConstants {
    private LspConstants() {}

    // Server info
    public static final String SERVER_NAME    = "Logo Language Server";
    public static final String SERVER_VERSION = "1.0.0";

    // Semantic token type names (indices match SEMANTIC_TOKENS_LEGEND order)
    public static final String TOKEN_TYPE_KEYWORD  = "keyword";
    public static final String TOKEN_TYPE_FUNCTION = "function";
    public static final String TOKEN_TYPE_VARIABLE = "variable";
    public static final String TOKEN_TYPE_NUMBER   = "number";
    public static final String TOKEN_TYPE_STRING   = "string";
    public static final String TOKEN_TYPE_COMMENT  = "comment";

    // Semantic token modifier names (indices match SEMANTIC_TOKENS_LEGEND modifier order)
    public static final String TOKEN_MODIFIER_DECLARATION = "declaration";

    // LSP protocol identifiers
    public static final String DIAGNOSTIC_SOURCE = "logo-lsp";
    public static final String MARKUP_MARKDOWN   = "markdown";

    // Log messages
    public static final String MSG_SERVER_STARTED    = "Logo LSP started (stdio)";
    public static final String MSG_SERVER_TERMINATED = "LSP server terminated";
}
