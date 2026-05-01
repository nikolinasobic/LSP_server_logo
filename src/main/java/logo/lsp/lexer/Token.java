package logo.lsp.lexer;

public class Token {
    public final TokenType type;
    public final String value;

    public final int line;
    public final int startCol;
    public int endCol;

    public Token(TokenType type, String value, int line, int startCol, int endCol) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.startCol = startCol;
        this.endCol = endCol;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", value='" + value + '\'' +
                ", line=" + line +
                ", startCol=" + startCol +
                ", endCol=" + endCol +
                '}';
    }
}
