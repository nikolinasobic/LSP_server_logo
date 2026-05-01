package logo.lsp.ast;

import logo.lsp.lexer.Token;

public class ParseError {
    public enum Severity {ERROR, WARNING}

    public final Severity severity;
    public final String message;
    public final int line;
    public final int startCol;
    public final int endCol;

    public ParseError(Severity severity, String message, int line, int startCol, int endCol) {
        this.severity = severity;
        this.message = message;
        this.line = line;
        this.startCol = startCol;
        this.endCol = endCol;
    }

    public static ParseError error(String msg, Token t){
        return new ParseError(Severity.ERROR, msg, t.line, t.startCol, t.endCol);
    }

    public static ParseError error(String msg, int line, int startCol, int endCol){
        return new ParseError(Severity.ERROR, msg, line, startCol, endCol);
    }

    public static  ParseError warning(String msg, Token t){
        return new ParseError(Severity.WARNING, msg, t.line, t.startCol, t.endCol);
    }
}
