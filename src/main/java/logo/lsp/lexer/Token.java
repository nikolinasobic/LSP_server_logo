package logo.lsp.lexer;

public record Token(TokenType type, String value, int line, int startCol, int endCol) {

    private static final String TO_STRING_FMT =
            "Token{type=%s, value='%s', line=%d, startCol=%d, endCol=%d}";

    @Override
    public String toString() {
        return String.format(TO_STRING_FMT, type, value, line, startCol, endCol);
    }
}
