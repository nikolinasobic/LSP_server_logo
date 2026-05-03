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
        KEYWORDS.put("setpos",       TokenType.SETPOS);
        KEYWORDS.put("setspeed",     TokenType.SETSPEED);
        KEYWORDS.put("home",         TokenType.HOME);

        // pen
        KEYWORDS.put("penup",        TokenType.PENUP);
        KEYWORDS.put("pu",           TokenType.PENUP);
        KEYWORDS.put("pendown",      TokenType.PENDOWN);
        KEYWORDS.put("pd",           TokenType.PENDOWN);
        KEYWORDS.put("pencolor",     TokenType.PENCOLOR);
        KEYWORDS.put("pc",           TokenType.PENCOLOR);
        KEYWORDS.put("setpencolor",  TokenType.SETPENCOLOR);
        KEYWORDS.put("setpensize",   TokenType.SETPENSIZE);
        KEYWORDS.put("fill",         TokenType.FILL);

        // screen
        KEYWORDS.put("clean",        TokenType.CLEAN);
        KEYWORDS.put("clearscreen",  TokenType.CLEARSCREEN);
        KEYWORDS.put("cs",           TokenType.CLEARSCREEN);
        KEYWORDS.put("hideturtle",   TokenType.HIDETURTLE);
        KEYWORDS.put("ht",           TokenType.HIDETURTLE);
        KEYWORDS.put("showturtle",   TokenType.SHOWTURTLE);
        KEYWORDS.put("st",           TokenType.SHOWTURTLE);
        KEYWORDS.put("label",        TokenType.LABEL);

        // variables/data
        KEYWORDS.put("make",         TokenType.MAKE);
        KEYWORDS.put("local",        TokenType.LOCAL);
        KEYWORDS.put("thing",        TokenType.THING);

        // I/O
        KEYWORDS.put("print",        TokenType.PRINT);
        KEYWORDS.put("pr",           TokenType.PRINT);
        KEYWORDS.put("show",         TokenType.SHOW);
        KEYWORDS.put("type",         TokenType.TYPE);

        // control flow
        KEYWORDS.put("if",           TokenType.IF);
        KEYWORDS.put("ifelse",       TokenType.IFELSE);
        KEYWORDS.put("test",         TokenType.TEST);
        KEYWORDS.put("iftrue",       TokenType.IFTRUE);
        KEYWORDS.put("ift",          TokenType.IFTRUE);
        KEYWORDS.put("iffalse",      TokenType.IFFALSE);
        KEYWORDS.put("iff",          TokenType.IFFALSE);
        KEYWORDS.put("repeat",       TokenType.REPEAT);
        KEYWORDS.put("forever",      TokenType.FOREVER);
        KEYWORDS.put("while",        TokenType.WHILE);
        KEYWORDS.put("until",        TokenType.UNTIL);
        KEYWORDS.put("for",          TokenType.FOR);

        // procedure definition
        KEYWORDS.put("to",           TokenType.TO);
        KEYWORDS.put("end",          TokenType.END);
        KEYWORDS.put("output",       TokenType.OUTPUT);
        KEYWORDS.put("op",           TokenType.OUTPUT);
        KEYWORDS.put("stop",         TokenType.STOP);

        KEYWORDS.put("run",          TokenType.RUN);
        KEYWORDS.put("apply",        TokenType.APPLY);

        // list
        KEYWORDS.put("list",         TokenType.LIST);
        KEYWORDS.put("first",        TokenType.FIRST);
        KEYWORDS.put("last",         TokenType.LAST);
        KEYWORDS.put("butfirst",     TokenType.BUTFIRST);
        KEYWORDS.put("bf",           TokenType.BUTFIRST);
        KEYWORDS.put("butlast",      TokenType.BUTLAST);
        KEYWORDS.put("bl",           TokenType.BUTLAST);
        KEYWORDS.put("item",         TokenType.ITEM);
        KEYWORDS.put("count",        TokenType.COUNT);
        KEYWORDS.put("sentence",     TokenType.SENTENCE);
        KEYWORDS.put("se",           TokenType.SENTENCE);
        KEYWORDS.put("fput",         TokenType.FPUT);
        KEYWORDS.put("lput",         TokenType.LPUT);

        // logic
        KEYWORDS.put("and",          TokenType.AND);
        KEYWORDS.put("or",           TokenType.OR);
        KEYWORDS.put("not",          TokenType.NOT);

        // arithmetic
        KEYWORDS.put("sum",          TokenType.SUM);
        KEYWORDS.put("difference",   TokenType.DIFFERENCE);
        KEYWORDS.put("product",      TokenType.PRODUCT);
        KEYWORDS.put("quotient",     TokenType.QUOTIENT);
        KEYWORDS.put("remainder",    TokenType.REMAINDER);
        KEYWORDS.put("modulo",       TokenType.MODULO);
        KEYWORDS.put("power",        TokenType.POWER);
        KEYWORDS.put("sqrt",         TokenType.SQRT);
        KEYWORDS.put("abs",          TokenType.ABS);
        KEYWORDS.put("minus",        TokenType.MINUS);

        // comparison
        KEYWORDS.put("equalp",        TokenType.EQUALP);
        KEYWORDS.put("equal?",        TokenType.EQUALP);
        KEYWORDS.put("notequalp",     TokenType.NOTEQUALP);
        KEYWORDS.put("notequal?",     TokenType.NOTEQUALP);
        KEYWORDS.put("lessp",         TokenType.LESSP);
        KEYWORDS.put("less?",         TokenType.LESSP);
        KEYWORDS.put("greaterp",      TokenType.GREATERP);
        KEYWORDS.put("greater?",      TokenType.GREATERP);
        KEYWORDS.put("lessequalp",    TokenType.LESSEQUALP);
        KEYWORDS.put("greaterequalp", TokenType.GREATEREQUALP);

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

            if (c == '\n') {
                final int col = pos - lineStart;
                tokens.add(new Token(TokenType.NEWLINE, TOK_NEWLINE, line, col, col + 1));
                advance();
                line++;
                lineStart = pos;
            } else if (c == '"') {
                readWordLiteral();
            } else if (c == ':') {
                readVariable();
            } else if (Character.isDigit(c) || (c == '-' && isDigitAhead())) {
                readNumber();
            } else if (c == '[') {
                emit(TokenType.LBRACKET, TOK_LBRACKET);
                advance();
            } else if (c == ']') {
                emit(TokenType.RBRACKET, TOK_RBRACKET);
                advance();
            } else if (c == '(') {
                emit(TokenType.LPAREN, TOK_LPAREN);
                advance();
            } else if (c == ')') {
                emit(TokenType.RPAREN, TOK_RPAREN);
                advance();
            } else if (c == '+') {
                emit(TokenType.PLUS, TOK_PLUS);
                advance();
            } else if (c == '-') {
                emit(TokenType.MINUS, TOK_MINUS);
                advance();
            } else if (c == '*') {
                emit(TokenType.STAR, TOK_STAR);
                advance();
            } else if (c == '/') {
                emit(TokenType.SLASH, TOK_SLASH);
                advance();
            } else if (c == '=') {
                emit(TokenType.EQUAL_SIGN, TOK_EQUAL);
                advance();
            } else if (c == '<') {
                emit(TokenType.LESS, TOK_LESS);
                advance();
            } else if (c == '>') {
                emit(TokenType.GREATER, TOK_GREATER);
                advance();
            } else if (c == '^') {
                emit(TokenType.CARET, TOK_CARET);
                advance();
            } else if (isIdentStart(c)) {
                readIdentifierOrKeyword();
            } else {
                final int col = pos - lineStart;
                tokens.add(new Token(TokenType.UNKNOWN, String.valueOf(c), line, col, col + 1));
                advance();
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
