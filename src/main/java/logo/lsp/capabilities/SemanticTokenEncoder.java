package logo.lsp.capabilities;

import logo.lsp.lexer.LogoLexer;
import logo.lsp.lexer.Token;
import logo.lsp.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

public class SemanticTokenEncoder {

    private static final int KEYWORD  = 0;
    private static final int FUNCTION = 1;
    private static final int VARIABLE = 2;
    private static final int NUMBER   = 3;
    private static final int STRING   = 4;

    public static List<Integer> encode(String source) {
        LogoLexer lexer = new LogoLexer(source);
        List<Token> tokens = lexer.tokenize();

        List<Integer> data = new ArrayList<>();
        int prevLine = 0;
        int prevCol  = 0;

        for (Token tok : tokens) {
            int typeIdx = mapTokenType(tok.type);
            if (typeIdx < 0) continue;

            int length = tok.endCol - tok.startCol;
            if (length <= 0) continue;

            int deltaLine = tok.line - prevLine;
            int deltaCol  = deltaLine == 0 ? tok.startCol - prevCol : tok.startCol;

            data.add(deltaLine);
            data.add(deltaCol);
            data.add(length);
            data.add(typeIdx);
            data.add(0); // no modifiers

            prevLine = tok.line;
            prevCol  = tok.startCol;
        }

        return data;
    }

    private static int mapTokenType(TokenType t) {
        return switch (t) {
            case FORWARD, BACK, LEFT, RIGHT,
                 PENUP, PENDOWN, PENCOLOR, SETPENCOLOR, SETPENSIZE,
                 CLEAN, CLEARSCREEN, HOME, FILL, LABEL,
                 SETX, SETY, SETXY, SETPOS, SETSPEED,
                 HIDETURTLE, SHOWTURTLE,
                 MAKE, LOCAL, THING,
                 PRINT, SHOW, TYPE,
                 IF, IFELSE, TEST, IFTRUE, IFFALSE,
                 REPEAT, FOREVER, WHILE, UNTIL, FOR,
                 TO, END, OUTPUT, STOP,
                 RUN, APPLY,
                 LIST, FIRST, LAST, BUTFIRST, BUTLAST, ITEM, COUNT,
                 SENTENCE, FPUT, LPUT,
                 AND, OR, NOT,
                 SUM, DIFFERENCE, PRODUCT, QUOTIENT, REMAINDER, MODULO, POWER, SQRT, ABS, MINUS,
                 EQUALP, NOTEQUALP, LESSP, GREATERP, LESSEQUALP, GREATEREQUALP, BOOLEAN
                    -> KEYWORD;
            case IDENTIFIER -> FUNCTION;
            case VARIABLE   -> VARIABLE;
            case NUMBER     -> NUMBER;
            case STRING     -> STRING;
            default         -> -1;
        };
    }

}
