package logo.lsp.lexer;

public class Token {
    private static final String TO_STRING_FMT =
            "Token{type=%s, value='%s', line=%d, startCol=%d, endCol=%d}";

    public final TokenType type;
    public final String    value;
    public final int       line;
    public final int       startCol;
    public final int       endCol;

    public Token(final TokenType type, final String value,
                 final int line, final int startCol, final int endCol) {
        this.type     = type;
        this.value    = value;
        this.line     = line;
        this.startCol = startCol;
        this.endCol   = endCol;
    }

    @Override
    public String toString() {
        return String.format(TO_STRING_FMT, type, value, line, startCol, endCol);
    }
}
