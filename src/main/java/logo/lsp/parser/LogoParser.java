package logo.lsp.parser;

import logo.lsp.ast.Node;
import logo.lsp.parser.ParseError;
import logo.lsp.lexer.Token;
import logo.lsp.lexer.TokenType;

import java.util.*;

public class LogoParser {

    private final Map<String, Token> procedureDefinitions = new LinkedHashMap<>();
    private final Map<String, Integer> procedureArities = new LinkedHashMap<>();

    // for "did you mean?" suggestions (all built-in commands)
    private static final List<String> BUILTIN_NAMES = List.of(
            "forward","fd","back","bk","left","lt","right","rt",
            "setx","sety","setxy","setpos","setspeed","home",
            "penup","pu","pendown","pd","pencolor","pc","setpencolor","setpensize",
            "clean","clearscreen","cs","hideturtle","ht","showturtle","st","fill","label",
            "make","local","thing",
            "print","pr","show","type",
            "if","ifelse","test","iftrue","ift","iffalse","iff",
            "repeat","forever","while","until","for",
            "to","end","output","op","stop",
            "run","apply",
            "list","first","last","butfirst","bf","butlast","bl","item","count",
            "sentence","se","fput","lput",
            "and","or","not",
            "sum","difference","product","quotient","remainder","modulo","power","sqrt","abs","minus",
            "equalp","notequalp","lessp","greaterp","lessequalp","greaterequalp"
    );

    private final List<Token> tokens;
    private int pos = 0;
    private final List<ParseError> errors = new ArrayList<>();

    public LogoParser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public ParseResult parse() {
        List<Node> stmts = new ArrayList<>();
        skipNewlines();
        while (!atEnd()) {
            try {
                Node stmt = parseStatement();
                if (stmt != null) stmts.add(stmt);
            } catch (ParseException e) {
                errors.add(e.error);
                synchronize();
            }
            skipNewlines();
        }
        Token eof = peek();
        return new ParseResult(new Node.Program(stmts, eof), errors,
                Collections.unmodifiableMap(procedureDefinitions));
    }

    private Node parseStatement() {
        Token t = peek();

        return switch (t.type) {
            case TO      -> parseProcedureDef();
            case MAKE    -> parseMake();
            case REPEAT  -> parseRepeat();
            case FOREVER -> parseForever();
            case IF      -> parseIf(false);
            case IFELSE  -> parseIf(true);
            case FOR     -> parseFor();
            case OUTPUT  -> parseOutput();
            case STOP    -> parseStop();
            case NEWLINE -> { skipNewlines(); yield null; }
            case EOF     -> null;

            //check if it is a known user-defined procedure or flag it as unknown.
            case IDENTIFIER -> parseUserOrUnknownCall();

            // any other built-in command token
            default -> {
                if (isCommandToken(t.type)) {
                    yield parseBuiltinCall();
                }
                //numbers, strings, variables, operators... arent valid statement starters.
                throw new ParseException(ParseError.error(
                        "Unexpected token '" + t.value +
                                "': expected a command or procedure name", t));
            }
        };
    }

    // user-defined procedure call  OR  unknown/misspelled command

    private Node parseUserOrUnknownCall() {
        Token nameToken = advance(); // consume the IDENTIFIER
        String name = nameToken.value.toLowerCase();

        boolean isKnownProcedure = procedureDefinitions.containsKey(name);

        if (!isKnownProcedure) {
            String suggestion = closestBuiltin(name);
            String msg = suggestion != null
                    ? "Unknown command '" + nameToken.value + "'. Did you mean '" + suggestion + "'?"
                    : "Unknown command '" + nameToken.value + "'";
            errors.add(ParseError.error(msg, nameToken));
            consumeRestOfLine();
            return new Node.CommandCall(nameToken, Collections.emptyList());
        }

        int arity = procedureArities.getOrDefault(name, 0);
        List<Node> args = new ArrayList<>();
        for (int i = 0; i < arity; i++) {
            if (atEnd() || check(TokenType.NEWLINE) || check(TokenType.RBRACKET)) {
                errors.add(ParseError.error(
                        "Procedure '" + nameToken.value + "' expects " + arity +
                                " argument(s) but got " + i, nameToken));
                break;
            }
            args.add(parseExpr());
        }
        return new Node.CommandCall(nameToken, args);
    }

    private void consumeRestOfLine() {
        while (!atEnd() && !check(TokenType.NEWLINE) && !check(TokenType.RBRACKET)) {
            advance();
        }
    }

    //built-in command call

    private Node.CommandCall parseBuiltinCall() {
        Token nameToken = advance();
        int arity = inferArity(nameToken.type);
        List<Node> args = new ArrayList<>();
        for (int i = 0; i < arity; i++) {
            if (atEnd() || check(TokenType.NEWLINE) || check(TokenType.RBRACKET)
                    || check(TokenType.EOF)) {
                errors.add(ParseError.error(
                        "'" + nameToken.value + "' expects " + arity +
                                " argument(s) but got " + i, nameToken));
                break;
            }
            args.add(parseExpr());
        }
        return new Node.CommandCall(nameToken, args);
    }

    //did you mean?

    private String closestBuiltin(String input) {
        if (input.length() < 2) return null;
        String best = null;
        int bestDist = Integer.MAX_VALUE;
        List<String> candidates = new ArrayList<>(BUILTIN_NAMES);
        candidates.addAll(procedureDefinitions.keySet());
        for (String candidate : candidates) {
            int d = levenshtein(input, candidate);

            int threshold = Math.max(2, input.length() / 3);
            if (d < bestDist && d <= threshold) {
                bestDist = d;
                best = candidate;
            }
        }
        return best;
    }

    private static int levenshtein(String a, String b) {
        int la = a.length(), lb = b.length();
        int[] prev = new int[lb + 1];
        for (int j = 0; j <= lb; j++) prev[j] = j;
        for (int i = 1; i <= la; i++) {
            int[] cur = new int[lb + 1];
            cur[0] = i;
            for (int j = 1; j <= lb; j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                cur[j] = Math.min(Math.min(cur[j-1] + 1, prev[j] + 1), prev[j-1] + cost);
            }
            prev = cur;
        }
        return prev[lb];
    }

    // TO name [:param ...] newline body END

    private Node.ProcedureDef parseProcedureDef() {
        Token toToken = consume(TokenType.TO);
        skipNewlines();

        Token nameToken = expectProcedureName("Expected procedure name after TO");
        procedureDefinitions.put(nameToken.value.toLowerCase(), nameToken);

        List<String> params = new ArrayList<>();
        while (check(TokenType.VARIABLE)) {
            params.add(advance().value.toLowerCase());
        }
        procedureArities.put(nameToken.value.toLowerCase(), params.size());
        skipNewlines();

        List<Node> body = new ArrayList<>();
        while (!atEnd() && !check(TokenType.END)) {
            skipNewlines();
            if (check(TokenType.END) || atEnd()) break;
            try {
                Node stmt = parseStatement();
                if (stmt != null) body.add(stmt);
            } catch (ParseException e) {
                errors.add(e.error);
                synchronize();
            }
            skipNewlines();
        }

        Token endToken = null;
        if (check(TokenType.END)) {
            endToken = advance();
        } else {
            errors.add(ParseError.error(
                    "Missing END for procedure '" + nameToken.value + "'", nameToken));
        }

        return new Node.ProcedureDef(toToken, nameToken, params, body, endToken);
    }

    // MAKE "varname expr

    private Node.MakeStatement parseMake() {
        Token makeToken = consume(TokenType.MAKE);
        String varName;
        if (check(TokenType.STRING)) {
            varName = advance().value.toLowerCase();
        } else if (check(TokenType.IDENTIFIER)) {
            varName = advance().value.toLowerCase();
        } else {
            throw new ParseException(
                    ParseError.error("Expected variable name after MAKE", peek()));
        }
        Node value = parseExpr();
        return new Node.MakeStatement(makeToken, varName, value);
    }

    // REPEAT n [ body ]

    private Node.RepeatStatement parseRepeat() {
        Token repeatToken = consume(TokenType.REPEAT);
        Node count = parseExpr();
        List<Node> body = parseBlock();
        return new Node.RepeatStatement(repeatToken, count, body);
    }

    // FOREVER [ body ]

    private Node parseForever() {
        Token token = consume(TokenType.FOREVER);
        List<Node> body = parseBlock();
        Node infinity = new Node.NumberLiteral(
                new Token(TokenType.NUMBER, "999999",
                        token.line, token.startCol, token.endCol));
        return new Node.RepeatStatement(token, infinity, body);
    }

    // IF cond [ then ]   /   IFELSE cond [ then ] [ else ]

    private Node.IfStatement parseIf(boolean isElse) {
        Token ifToken = advance();
        Node condition = parseExpr();
        List<Node> thenBody = parseBlock();
        List<Node> elseBody = isElse ? parseBlock() : Collections.emptyList();
        return new Node.IfStatement(ifToken, condition, thenBody, elseBody);
    }

    // FOR [ var start end [step] ] [ body ]

    private Node.ForStatement parseFor() {
        Token forToken = consume(TokenType.FOR);
        expect(TokenType.LBRACKET, "Expected '[' after FOR");
        skipNewlines();

        String varName;
        if (check(TokenType.VARIABLE)) {
            varName = advance().value.toLowerCase();
        } else if (check(TokenType.IDENTIFIER)) {
            varName = advance().value.toLowerCase();
        } else {
            throw new ParseException(
                    ParseError.error("Expected variable name in FOR header", peek()));
        }

        Node start = parseExpr();
        Node end   = parseExpr();
        Node step  = check(TokenType.RBRACKET) ? null : parseExpr();
        expect(TokenType.RBRACKET, "Expected ']' to close FOR header");
        skipNewlines();
        List<Node> body = parseBlock();
        return new Node.ForStatement(forToken, varName, start, end, step, body);
    }

    // OUTPUT expr  /  STOP

    private Node.OutputStatement parseOutput() {
        Token t = consume(TokenType.OUTPUT);
        Node value = parseExpr();
        return new Node.OutputStatement(t, value);
    }

    private Node.OutputStatement parseStop() {
        Token t = consume(TokenType.STOP);
        return new Node.OutputStatement(t, null);
    }

    // arity table for built-in commands

    private int inferArity(TokenType t) {
        return switch (t) {
            case FORWARD, BACK, LEFT, RIGHT,
                 SETX, SETY, LABEL, SETSPEED, SETPENSIZE, PENCOLOR,
                 PRINT, SHOW, TYPE,
                 SQRT, ABS, MINUS, NOT, FIRST, LAST,
                 BUTFIRST, BUTLAST, COUNT, THING -> 1;

            case SETXY, SETPOS, SETPENCOLOR,
                 SUM, DIFFERENCE, PRODUCT, QUOTIENT, REMAINDER, MODULO, POWER,
                 EQUALP, NOTEQUALP, LESSP, GREATERP, LESSEQUALP, GREATEREQUALP,
                 AND, OR, FPUT, LPUT, ITEM,
                 SENTENCE, LIST -> 2;

            case PENUP, PENDOWN, CLEAN, CLEARSCREEN, HOME,
                 HIDETURTLE, SHOWTURTLE, FILL, STOP -> 0;

            default -> 0;
        };
    }

    // Block  [ statements ]

    private List<Node> parseBlock() {
        skipNewlines();
        expect(TokenType.LBRACKET, "Expected '['");
        skipNewlines();
        List<Node> stmts = new ArrayList<>();
        while (!atEnd() && !check(TokenType.RBRACKET)) {
            skipNewlines();
            if (check(TokenType.RBRACKET) || atEnd()) break;
            try {
                Node s = parseStatement();
                if (s != null) stmts.add(s);
            } catch (ParseException e) {
                errors.add(e.error);
                synchronize();
            }
            skipNewlines();
        }
        if (check(TokenType.RBRACKET)) {
            advance();
        } else {
            errors.add(ParseError.error("Missing ']'", peek()));
        }
        return stmts;
    }

    // Expression parsing (precedence climbing)

    private Node parseExpr() {
        return parseComparison();
    }

    private Node parseComparison() {
        Node left = parseAddSub();
        while (checkAny(TokenType.EQUAL_SIGN, TokenType.LESS, TokenType.GREATER)) {
            Token op = advance();
            Node right = parseAddSub();
            left = new Node.BinaryExpr(op, left, right);
        }
        return left;
    }

    private Node parseAddSub() {
        Node left = parseMulDiv();
        while (checkAny(TokenType.PLUS, TokenType.MINUS_OP)) {
            Token op = advance();
            Node right = parseMulDiv();
            left = new Node.BinaryExpr(op, left, right);
        }
        return left;
    }

    private Node parseMulDiv() {
        Node left = parseUnary();
        while (checkAny(TokenType.STAR, TokenType.SLASH, TokenType.CARET)) {
            Token op = advance();
            Node right = parseUnary();
            left = new Node.BinaryExpr(op, left, right);
        }
        return left;
    }

    private Node parseUnary() {
        if (checkAny(TokenType.MINUS_OP, TokenType.MINUS)) {
            Token op = advance();
            return new Node.UnaryExpr(op, parseUnary());
        }
        return parsePrimary();
    }

    private Node parsePrimary() {
        Token t = peek();
        return switch (t.type) {
            case NUMBER   -> new Node.NumberLiteral(advance());
            case STRING   -> new Node.WordLiteral(advance());
            case BOOLEAN  -> new Node.BooleanLiteral(advance());
            case VARIABLE -> new Node.VariableRef(advance());
            case LBRACKET -> {
                Token lb = advance();
                List<Node> elems = new ArrayList<>();
                while (!atEnd() && !check(TokenType.RBRACKET)) {
                    elems.add(parsePrimary());
                    skipNewlines();
                }
                expect(TokenType.RBRACKET, "Expected ']'");
                yield new Node.ListLiteral(lb, elems);
            }
            case LPAREN -> {
                advance();
                Node inner = parseExpr();
                expect(TokenType.RPAREN, "Expected ')'");
                yield inner;
            }
            case IDENTIFIER -> new Node.CommandCall(advance(), Collections.emptyList());
            default -> {
                if (isCommandToken(t.type)) {
                    yield parseBuiltinCall();
                }
                throw new ParseException(ParseError.error(
                        "Unexpected token '" + t.value + "' in expression", t));
            }
        };
    }

    private boolean isCommandToken(TokenType type) {
        return switch (type) {
            case FORWARD, BACK, LEFT, RIGHT,
                 SETX, SETY, SETXY, SETPOS,
                 PENUP, PENDOWN, PENCOLOR, SETPENCOLOR, SETPENSIZE,
                 CLEAN, CLEARSCREEN, HOME, FILL, LABEL,
                 HIDETURTLE, SHOWTURTLE, SETSPEED,
                 PRINT, SHOW, TYPE,
                 SUM, DIFFERENCE, PRODUCT, QUOTIENT, REMAINDER, MODULO, POWER,
                 SQRT, ABS, MINUS, NOT,
                 EQUALP, NOTEQUALP, LESSP, GREATERP, LESSEQUALP, GREATEREQUALP,
                 AND, OR,
                 LIST, FIRST, LAST, BUTFIRST, BUTLAST, ITEM, COUNT, SENTENCE, FPUT, LPUT,
                 THING, MAKE, OUTPUT, STOP, RUN, APPLY -> true;
            default -> false;
        };
    }

    private void skipNewlines() {
        while (check(TokenType.NEWLINE)) advance();
    }

    private boolean atEnd() {
        return peek().type == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(pos);
    }

    private Token advance() {
        Token t = tokens.get(pos);
        if (t.type != TokenType.EOF) pos++;
        return t;
    }

    private boolean check(TokenType type) {
        return peek().type == type;
    }

    private boolean checkAny(TokenType... types) {
        TokenType cur = peek().type;
        for (TokenType t : types) if (cur == t) return true;
        return false;
    }

    private Token consume(TokenType type) {
        if (!check(type)) {
            throw new ParseException(ParseError.error(
                    "Expected " + type + " but got '" + peek().value + "'", peek()));
        }
        return advance();
    }

    private Token expect(TokenType type, String msg) {
        if (!check(type)) {
            errors.add(ParseError.error(msg, peek()));
            Token cur = peek();
            return new Token(type, "", cur.line, cur.startCol, cur.startCol);
        }
        return advance();
    }

    private Token expectProcedureName(String msg) {
        Token t = peek();
        //built-in keywords cant be redefined (in real Logo they can)
        if (t.type == TokenType.IDENTIFIER) return advance();
        throw new ParseException(ParseError.error(msg, t));
    }

    private void synchronize() {
        while (!atEnd()) {
            TokenType t = peek().type;
            if (t == TokenType.NEWLINE || t == TokenType.END || t == TokenType.TO) {
                skipNewlines();
                return;
            }
            advance();
        }
    }

    static class ParseException extends RuntimeException {
        final ParseError error;
        ParseException(ParseError e) {
            super(e.message);
            this.error = e;
        }
    }
}