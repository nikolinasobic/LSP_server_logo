package logo.lsp.lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogoLexer {

    // token value constants for single-character and special tokens
    private static final String TOK_NEWLINE  = "\n";
    private static final String TOK_LBRACKET = "[";
    private static final String TOK_RBRACKET = "]";
    private static final String TOK_LPAREN   = "(";
    private static final String TOK_RPAREN   = ")";
    private static final String TOK_PLUS     = "+";
    private static final String TOK_MINUS    = "-";
    private static final String TOK_STAR     = "*";
    private static final String TOK_SLASH    = "/";
    private static final String TOK_EQUAL    = "=";
    private static final String TOK_LESS     = "<";
    private static final String TOK_GREATER  = ">";
    private static final String TOK_CARET    = "^";
    private static final String TOK_COLON    = ":";
    private static final String TOK_EOF      = "";

    private static final Map<String, TokenType> KEYWORDS = new HashMap<>();

    static {
        // motion
        KEYWORDS.put("forward",      TokenType.FORWARD);
        KEYWORDS.put("fd",           TokenType.FORWARD);
        KEYWORDS.put("back",         TokenType.BACK);
        KEYWORDS.put("bk",           TokenType.BACK);
        KEYWORDS.put("left",         TokenType.LEFT);
        KEYWORDS.put("lt",           TokenType.LEFT);
        KEYWORDS.put("right",        TokenType.RIGHT);
        KEYWORDS.put("rt",           TokenType.RIGHT);
        KEYWORDS.put("setx",         TokenType.SETX);
        KEYWORDS.put("sety",         TokenType.SETY);
        KEYWORDS.put("setxy",        TokenType.SETXY);
        KEYWORDS.put("setheading",   TokenType.SETHEADING);
        KEYWORDS.put("seth",         TokenType.SETHEADING);
        KEYWORDS.put("sh",           TokenType.SETHEADING);
        KEYWORDS.put("home",         TokenType.HOME);
        KEYWORDS.put("arc",          TokenType.ARC);
        KEYWORDS.put("ellipse",      TokenType.ELLIPSE);

        // motion queries
        KEYWORDS.put("pos",          TokenType.POS);
        KEYWORDS.put("xcor",         TokenType.XCOR);
        KEYWORDS.put("ycor",         TokenType.YCOR);
        KEYWORDS.put("heading",      TokenType.HEADING);
        KEYWORDS.put("towards",      TokenType.TOWARDS);

        // turtle/window control
        KEYWORDS.put("wrap",         TokenType.WRAP);
        KEYWORDS.put("window",       TokenType.WINDOW);
        KEYWORDS.put("fence",        TokenType.FENCE);
        KEYWORDS.put("hideturtle",   TokenType.HIDETURTLE);
        KEYWORDS.put("ht",           TokenType.HIDETURTLE);
        KEYWORDS.put("showturtle",   TokenType.SHOWTURTLE);
        KEYWORDS.put("st",           TokenType.SHOWTURTLE);

        // turtle/window queries
        KEYWORDS.put("shownp",       TokenType.SHOWNP);
        KEYWORDS.put("shown?",       TokenType.SHOWNP);

        // pen
        KEYWORDS.put("penup",        TokenType.PENUP);
        KEYWORDS.put("pu",           TokenType.PENUP);
        KEYWORDS.put("pendown",      TokenType.PENDOWN);
        KEYWORDS.put("pd",           TokenType.PENDOWN);
        KEYWORDS.put("pencolor",     TokenType.PENCOLOR);
        KEYWORDS.put("pc",           TokenType.PENCOLOR);
        KEYWORDS.put("setcolor",     TokenType.SETCOLOR);
        KEYWORDS.put("setwidth",     TokenType.SETWIDTH);
        KEYWORDS.put("fill",         TokenType.FILL);
        KEYWORDS.put("filled",       TokenType.FILLED);
        KEYWORDS.put("label",        TokenType.LABEL);
        KEYWORDS.put("setlabelheight", TokenType.SETLABELHEIGHT);
        KEYWORDS.put("changeshape",  TokenType.CHANGESHAPE);
        KEYWORDS.put("csh",          TokenType.CHANGESHAPE);

        // pen queries
        KEYWORDS.put("pendownp",     TokenType.PENDOWNP);
        KEYWORDS.put("pendown?",     TokenType.PENDOWNP);
        KEYWORDS.put("pensize",      TokenType.PENSIZE);
        KEYWORDS.put("labelsize",    TokenType.LABELSIZE);

        // screen
        KEYWORDS.put("clean",        TokenType.CLEAN);
        KEYWORDS.put("clearscreen",  TokenType.CLEARSCREEN);
        KEYWORDS.put("cs",           TokenType.CLEARSCREEN);

        // variables/data
        KEYWORDS.put("make",         TokenType.MAKE);
        KEYWORDS.put("local",        TokenType.LOCAL);
        KEYWORDS.put("thing",        TokenType.THING);
        KEYWORDS.put("name",         TokenType.NAME);
        KEYWORDS.put("localmake",    TokenType.LOCALMAKE);

        // I/O
        KEYWORDS.put("print",        TokenType.PRINT);
        KEYWORDS.put("pr",           TokenType.PRINT);
        KEYWORDS.put("show",         TokenType.SHOW);
        KEYWORDS.put("readword",     TokenType.READWORD);
        KEYWORDS.put("readlist",     TokenType.READLIST);

        // control flow
        KEYWORDS.put("if",           TokenType.IF);
        KEYWORDS.put("ifelse",       TokenType.IFELSE);
        KEYWORDS.put("test",         TokenType.TEST);
        KEYWORDS.put("iftrue",       TokenType.IFTRUE);
        KEYWORDS.put("ift",          TokenType.IFTRUE);
        KEYWORDS.put("iffalse",      TokenType.IFFALSE);
        KEYWORDS.put("iff",          TokenType.IFFALSE);
        KEYWORDS.put("repeat",       TokenType.REPEAT);
        KEYWORDS.put("while",        TokenType.WHILE);
        KEYWORDS.put("until",        TokenType.UNTIL);
        KEYWORDS.put("for",          TokenType.FOR);
        KEYWORDS.put("dotimes",      TokenType.DOTIMES);
        KEYWORDS.put("do.while",     TokenType.DO_WHILE);
        KEYWORDS.put("do.until",     TokenType.DO_UNTIL);
        KEYWORDS.put("wait",         TokenType.WAIT);
        KEYWORDS.put("bye",          TokenType.BYE);
        KEYWORDS.put("repcount",     TokenType.REPCOUNT);

        // procedure definition
        KEYWORDS.put("to",           TokenType.TO);
        KEYWORDS.put("end",          TokenType.END);

        KEYWORDS.put("define",       TokenType.DEFINE);
        KEYWORDS.put("def",          TokenType.DEF);

        // list
        KEYWORDS.put("list",         TokenType.LIST);
        KEYWORDS.put("first",        TokenType.FIRST);
        KEYWORDS.put("last",         TokenType.LAST);
        KEYWORDS.put("butfirst",     TokenType.BUTFIRST);
        KEYWORDS.put("bf",           TokenType.BUTFIRST);
        KEYWORDS.put("butlast",      TokenType.BUTLAST);
        KEYWORDS.put("bl",           TokenType.BUTLAST);
        KEYWORDS.put("item",         TokenType.ITEM);
        KEYWORDS.put("pick",         TokenType.PICK);

        // arithmetic
        KEYWORDS.put("sum",          TokenType.SUM);
        KEYWORDS.put("modulo",       TokenType.MODULO);
        KEYWORDS.put("power",        TokenType.POWER);
        KEYWORDS.put("minus",        TokenType.KEYWORD_MINUS);
        KEYWORDS.put("random",       TokenType.RANDOM);

        // comparison
        KEYWORDS.put("equalp",       TokenType.EQUALP);
        KEYWORDS.put("equal?",       TokenType.EQUALP);
        KEYWORDS.put("notequalp",    TokenType.NOTEQUALP);
        KEYWORDS.put("notequal?",    TokenType.NOTEQUALP);

        // predicates
        KEYWORDS.put("wordp",        TokenType.WORDP);
        KEYWORDS.put("word?",        TokenType.WORDP);
        KEYWORDS.put("listp",        TokenType.LISTP);
        KEYWORDS.put("list?",        TokenType.LISTP);
        KEYWORDS.put("array",        TokenType.ARRAY);
        KEYWORDS.put("arrayp",       TokenType.ARRAYP);
        KEYWORDS.put("array?",       TokenType.ARRAYP);
        KEYWORDS.put("numberp",      TokenType.NUMBERP);
        KEYWORDS.put("number?",      TokenType.NUMBERP);
        KEYWORDS.put("emptyp",       TokenType.EMPTYP);
        KEYWORDS.put("empty?",       TokenType.EMPTYP);
        KEYWORDS.put("beforep",      TokenType.BEFOREP);
        KEYWORDS.put("before?",      TokenType.BEFOREP);
        KEYWORDS.put("substringp",   TokenType.SUBSTRINGP);
        KEYWORDS.put("substring?",   TokenType.SUBSTRINGP);

        // booleans
        KEYWORDS.put("true",  TokenType.BOOLEAN);
        KEYWORDS.put("false", TokenType.BOOLEAN);
    }

    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    private int pos       = 0;
    private int line      = 0;
    private int lineStart = 0;

    public LogoLexer(final String source) {
        this.source = source;
    }

    public List<Token> tokenize() {
        while (!atEnd()) {
            skipWhitespaceAndComments();
            if (atEnd()) break;

            final char c = peek();

            switch (c) {
                case '\n' -> {
                    final int col = pos - lineStart;
                    tokens.add(new Token(TokenType.NEWLINE, TOK_NEWLINE, line, col, col + 1));
                    advance();
                    line++;
                    lineStart = pos;
                }
                case '"'  -> readWordLiteral();
                case ':'  -> readVariable();
                case '['  -> { emit(TokenType.LBRACKET,   TOK_LBRACKET); advance(); }
                case ']'  -> { emit(TokenType.RBRACKET,   TOK_RBRACKET); advance(); }
                case '('  -> { emit(TokenType.LPAREN,     TOK_LPAREN);   advance(); }
                case ')'  -> { emit(TokenType.RPAREN,     TOK_RPAREN);   advance(); }
                case '+'  -> { emit(TokenType.PLUS,       TOK_PLUS);     advance(); }
                case '-'  -> { if (isDigitAhead()) readNumber();
                               else { emit(TokenType.MINUS, TOK_MINUS);  advance(); } }
                case '*'  -> { emit(TokenType.STAR,       TOK_STAR);     advance(); }
                case '/'  -> { emit(TokenType.SLASH,      TOK_SLASH);    advance(); }
                case '='  -> { emit(TokenType.EQUAL_SIGN, TOK_EQUAL);    advance(); }
                case '<'  -> { emit(TokenType.LESS,       TOK_LESS);     advance(); }
                case '>'  -> { emit(TokenType.GREATER,    TOK_GREATER);  advance(); }
                case '^'  -> { emit(TokenType.CARET,      TOK_CARET);    advance(); }
                default   -> {
                    if (Character.isDigit(c))    readNumber();
                    else if (isIdentStart(c))    readIdentifierOrKeyword();
                    else {
                        final int col = pos - lineStart;
                        tokens.add(new Token(TokenType.UNKNOWN, String.valueOf(c), line, col, col + 1));
                        advance();
                    }
                }
            }
        }
        tokens.add(new Token(TokenType.EOF, TOK_EOF, line, pos - lineStart, pos - lineStart));
        return tokens;
    }

    private void readWordLiteral() {
        final int col = pos - lineStart;
        advance(); // consume "
        final var sb = new StringBuilder();
        while (!atEnd() && !Character.isWhitespace(peek()) && peek() != ']' && peek() != ')') {
            sb.append(advance());
        }
        tokens.add(new Token(TokenType.STRING, sb.toString(), line, col, pos - lineStart));
    }

    private void readNumber() {
        final int col = pos - lineStart;
        final var sb  = new StringBuilder();
        if (peek() == '-') sb.append(advance());
        while (!atEnd() && Character.isDigit(peek())) sb.append(advance());
        if (!atEnd() && peek() == '.') {
            sb.append(advance());
            while (!atEnd() && Character.isDigit(peek())) sb.append(advance());
        }
        tokens.add(new Token(TokenType.NUMBER, sb.toString(), line, col, pos - lineStart));
    }

    private void readVariable() {
        final int col = pos - lineStart;
        advance(); // consume :
        final var sb = new StringBuilder();
        while (!atEnd() && isIdentPart(peek())) {
            sb.append(advance());
        }
        if (sb.isEmpty()) {
            tokens.add(new Token(TokenType.COLON, TOK_COLON, line, col, pos - lineStart));
        } else {
            tokens.add(new Token(TokenType.VARIABLE, sb.toString(), line, col, pos - lineStart));
        }
    }

    private void skipWhitespaceAndComments() {
        while (!atEnd()) {
            final char c = peek();
            if (c == ';') {
                while (!atEnd() && peek() != '\n') advance();
            } else if (c == '\r') {
                advance();
            } else if (c == ' ' || c == '\t') {
                advance();
            } else {
                break;
            }
        }
    }

    private void readIdentifierOrKeyword() {
        final int col = pos - lineStart;
        final var sb  = new StringBuilder();
        while (!atEnd() && isIdentPart(peek())) sb.append(advance());
        final String word  = sb.toString();
        final String lower = word.toLowerCase();
        final TokenType type = KEYWORDS.getOrDefault(lower, TokenType.IDENTIFIER);
        tokens.add(new Token(type, word, line, col, pos - lineStart));
    }

    private boolean atEnd() {
        return pos >= source.length();
    }

    private char peek() {
        return source.charAt(pos);
    }

    private char advance() {
        return source.charAt(pos++);
    }

    private boolean isIdentStart(final char c) {
        return Character.isLetter(c) || c == '_' || c == '?' || c == '!';
    }

    private boolean isIdentPart(final char c) {
        return Character.isLetterOrDigit(c) || c == '_' || c == '?' || c == '!' || c == '.';
    }

    private boolean isDigitAhead() {
        return pos + 1 < source.length() && Character.isDigit(source.charAt(pos + 1));
    }

    private void emit(final TokenType type, final String value) {
        final int col = pos - lineStart;
        tokens.add(new Token(type, value, line, col, col + value.length()));
    }
}
