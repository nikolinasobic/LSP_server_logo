package logo.lsp.lexer;

public record Token(TokenType type, String value, int line, int startCol, int endCol) {

    @Override
    public String toString() {
        return String.format("Token{type=%s, value='%s', line=%d, startCol=%d, endCol=%d}",
                type, value, line, startCol, endCol);
    }
}
