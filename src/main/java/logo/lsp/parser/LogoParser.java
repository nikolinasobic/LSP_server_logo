package logo.lsp.parser;

import logo.lsp.ast.Node;
import logo.lsp.lexer.Token;
import logo.lsp.lexer.TokenType;

import java.util.*;

public class LogoParser {

    // error message format strings
    private static final String ERR_UNEXPECTED_TOKEN_FMT       = "Unexpected token '%s': expected a command or procedure name";
    private static final String ERR_UNKNOWN_CMD_SUGGESTION_FMT = "Unknown command '%s'. Did you mean '%s'?";
    private static final String ERR_UNKNOWN_CMD_FMT            = "Unknown command '%s'";
    private static final String ERR_PROCEDURE_ARITY_FMT        = "Procedure '%s' expects %d argument(s) but got %d";
    private static final String ERR_COMMAND_ARITY_FMT          = "'%s' expects %d argument(s) but got %d";
    private static final String ERR_MISSING_END_FMT            = "Missing END for procedure '%s'";
    private static final String ERR_EXPECTED_TYPE_FMT          = "Expected %s but got '%s'";
    private static final String ERR_UNEXPECTED_EXPR_TOKEN_FMT  = "Unexpected token '%s' in expression";

    // fixed error messages
    private static final String ERR_EXPECTED_PROC_NAME         = "Expected procedure name after TO";
    private static final String ERR_EXPECTED_PROC_NAME_DEFINE  = "Expected procedure name after DEFINE";
    private static final String ERR_EXPECTED_VAR_NAME_MAKE     = "Expected variable name after MAKE";
    private static final String ERR_EXPECTED_VAR_NAME_LOCAL    = "Expected variable name after LOCAL";
    private static final String ERR_EXPECTED_VAR_NAME_LOCALMAKE = "Expected variable name after LOCALMAKE";
    private static final String ERR_EXPECTED_VAR_NAME_NAME     = "Expected variable name after NAME value";
    private static final String ERR_EXPECTED_LBRACKET_FOR      = "Expected '[' after FOR";
    private static final String ERR_EXPECTED_VAR_NAME_FOR      = "Expected variable name in FOR header";
    private static final String ERR_EXPECTED_RBRACKET_FOR      = "Expected ']' to close FOR header";
    private static final String ERR_EXPECTED_LBRACKET_DOTIMES  = "Expected '[' after DOTIMES";
    private static final String ERR_EXPECTED_VAR_NAME_DOTIMES  = "Expected variable name in DOTIMES header";
    private static final String ERR_EXPECTED_RBRACKET_DOTIMES  = "Expected ']' to close DOTIMES header";
    private static final String ERR_EXPECTED_LBRACKET          = "Expected '['";
    private static final String ERR_EXPECTED_RBRACKET          = "Expected ']'";
    private static final String ERR_EXPECTED_RPAREN            = "Expected ')'";
    private static final String ERR_MISSING_RBRACKET           = "Missing ']'";
    private static final String ERR_EXPECTED_DEFINE_SPEC       = "Expected '[[params][body]]' after DEFINE name";
    private static final String ERR_EXPECTED_RBRACKET_DEFINE   = "Expected ']' to close DEFINE spec";

    // for did you mean? suggestions and autocomplete
    public static final List<String> BUILTIN_NAMES = List.of(
            "forward","fd","back","bk","left","lt","right","rt",
            "setx","sety","setxy","setpos","setheading","seth","sh","home","arc","ellipse",
            "pos","xcor","ycor","heading","towards",
            "wrap","window","fence","hideturtle","ht","showturtle","st",
            "shownp","shown?",
            "penup","pu","pendown","pd","pencolor","pc",
            "setpencolor","setcolor","setpensize","setwidth",
            "fill","filled","label","setlabelheight","changeshape","csh",
            "pendownp","pendown?","pensize","labelsize",
            "clean","clearscreen","cs",
            "make","local","thing","name","localmake",
            "print","pr","show","readword","readlist",
            "if","ifelse","test","iftrue","ift","iffalse","iff",
            "repeat","while","until","for","dotimes","do.while","do.until",
            "wait","bye","repcount",
            "to","end","output","op","stop","define","def",
            "list","first","last","butfirst","bf","butlast","bl","item","pick",
            "sum","modulo","power","minus","random",
            "equalp","equal?","notequalp","notequal?",
            "wordp","word?","listp","list?","array","arrayp","array?","numberp","number?","emptyp","empty?",
            "beforep","before?","substringp","substring?"
    );

    private final Map<String, Token>       procedureDefinitions = new LinkedHashMap<>();
    private final Map<String, Integer>     procedureArities     = new LinkedHashMap<>();
    private final Map<String, List<String>> procedureParams     = new LinkedHashMap<>();
    private final Map<String, Token>       variableDefinitions  = new LinkedHashMap<>();

    private final List<Token> tokens;
    private int pos = 0;
    private final List<ParseError> errors = new ArrayList<>();

    public LogoParser(final List<Token> tokens) {
        this.tokens = tokens;
    }

    public ParseResult parse() {
        final var stmts = new ArrayList<Node>();
        skipNewlines();
        while (!atEnd()) {
            try {
                final var stmt = parseStatement();
                if (stmt != null) stmts.add(stmt);
            } catch (ParseException e) {
                errors.add(e.error);
                synchronize();
            }
            skipNewlines();
        }
        final var eof = peek();
        return new ParseResult(new Node.Program(stmts, eof), errors,
                Collections.unmodifiableList(tokens),
                Collections.unmodifiableMap(procedureDefinitions),
                Collections.unmodifiableMap(procedureParams),
                Collections.unmodifiableMap(variableDefinitions));
    }

    private Node parseStatement() {
        final Token t = peek();

        return switch (t.type()) {
            case TO              -> parseProcedureDef();
            case DEFINE          -> parseDefine();
            case MAKE            -> parseMake();
            case LOCAL           -> parseLocal();
            case LOCALMAKE       -> parseLocalMake();
            case NAME            -> parseName();
            case REPEAT          -> parseRepeat();
            case WHILE, UNTIL    -> parseWhile();
            case DO_WHILE, DO_UNTIL -> parseDoWhileOrUntil();
            case DOTIMES         -> parseDotimes();
            case FILLED          -> parseFilled();
            case IF              -> parseIf(false);
            case IFELSE          -> parseIf(true);
            case TEST            -> parseTest();
            case IFTRUE          -> parseIfTrueOrFalse(false);
            case IFFALSE         -> parseIfTrueOrFalse(true);
            case FOR             -> parseFor();
            case OUTPUT          -> parseOutput();
            case STOP            -> parseStop();
            case NEWLINE -> { skipNewlines(); yield null; }
            case EOF     -> null;

            // known user-defined procedure or unknown command
            case IDENTIFIER -> parseUserOrUnknownCall();

            // any other built-in command token
            default -> {
                if (isCommandToken(t.type())) {
                    yield parseBuiltinCall();
                }
                throw new ParseException(ParseError.error(
                        String.format(ERR_UNEXPECTED_TOKEN_FMT, t.value()), t));
            }
        };
    }

    private Node parseUserOrUnknownCall() {
        final var nameToken = advance();
        final var name      = nameToken.value().toLowerCase();

        if (!procedureDefinitions.containsKey(name)) {
            final var suggestion = closestBuiltin(name);
            final var msg = suggestion != null
                    ? String.format(ERR_UNKNOWN_CMD_SUGGESTION_FMT, nameToken.value(), suggestion)
                    : String.format(ERR_UNKNOWN_CMD_FMT, nameToken.value());
            errors.add(ParseError.error(msg, nameToken));
            consumeRestOfLine();
            return new Node.CommandCall(nameToken, Collections.emptyList());
        }

        final int arity = procedureArities.getOrDefault(name, 0);
        final var args  = new ArrayList<Node>();
        for (int i = 0; i < arity; i++) {
            if (atEnd() || check(TokenType.NEWLINE) || check(TokenType.RBRACKET)) {
                errors.add(ParseError.error(
                        String.format(ERR_PROCEDURE_ARITY_FMT, nameToken.value(), arity, i),
                        nameToken));
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

    private Node.CommandCall parseBuiltinCall() {
        final var nameToken = advance();
        final int arity     = inferArity(nameToken.type());
        final var args      = new ArrayList<Node>();
        for (int i = 0; i < arity; i++) {
            if (atEnd() || check(TokenType.NEWLINE) || check(TokenType.RBRACKET)) {
                errors.add(ParseError.error(
                        String.format(ERR_COMMAND_ARITY_FMT, nameToken.value(), arity, i),
                        nameToken));
                break;
            }
            args.add(parseExpr());
        }
        return new Node.CommandCall(nameToken, args);
    }

    private String closestBuiltin(final String input) {
        if (input.length() < 2) return null;
        String best  = null;
        int bestDist = Integer.MAX_VALUE;
        final int threshold = Math.max(2, input.length() / 3);
        for (final String candidate : BUILTIN_NAMES) {
            final int d = levenshtein(input, candidate);
            if (d < bestDist && d <= threshold) { bestDist = d; best = candidate; }
        }
        for (final String candidate : procedureDefinitions.keySet()) {
            final int d = levenshtein(input, candidate);
            if (d < bestDist && d <= threshold) { bestDist = d; best = candidate; }
        }
        return best;
    }

    private static int levenshtein(final String a, final String b) {
        final int la = a.length();
        final int lb = b.length();
        int[] prev = new int[lb + 1];
        for (int j = 0; j <= lb; j++) prev[j] = j;
        for (int i = 1; i <= la; i++) {
            final var cur = new int[lb + 1];
            cur[0] = i;
            for (int j = 1; j <= lb; j++) {
                final int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                cur[j] = Math.min(Math.min(cur[j - 1] + 1, prev[j] + 1), prev[j - 1] + cost);
            }
            prev = cur;
        }
        return prev[lb];
    }

    // TO name [:param ...] newline body END

    private Node.ProcedureDef parseProcedureDef() {
        final var toToken   = consume(TokenType.TO);
        skipNewlines();
        final var nameToken = expectProcedureName(ERR_EXPECTED_PROC_NAME);
        final var name      = nameToken.value().toLowerCase();
        procedureDefinitions.put(name, nameToken);

        final var params = new ArrayList<String>();
        while (check(TokenType.VARIABLE)) {
            final var paramToken = advance();
            final var paramName  = paramToken.value().toLowerCase();
            params.add(paramName);
            variableDefinitions.putIfAbsent(paramName, paramToken);
        }
        procedureArities.put(name, params.size());
        procedureParams.put(name, Collections.unmodifiableList(params));

        final var body = new ArrayList<Node>();
        while (true) {
            skipNewlines();
            if (atEnd() || check(TokenType.END)) break;
            try {
                final var stmt = parseStatement();
                if (stmt != null) body.add(stmt);
            } catch (ParseException e) {
                errors.add(e.error);
                synchronize();
            }
        }

        final Token endToken;
        if (check(TokenType.END)) {
            endToken = advance();
        } else {
            errors.add(ParseError.error(
                    String.format(ERR_MISSING_END_FMT, nameToken.value()), nameToken));
            endToken = null;
        }

        return new Node.ProcedureDef(toToken, nameToken, params, body, endToken);
    }

    // MAKE "varname expr

    private Node.MakeStatement parseMake() {
        final var makeToken    = consume(TokenType.MAKE);
        if (!check(TokenType.STRING) && !check(TokenType.IDENTIFIER)) {
            throw new ParseException(ParseError.error(ERR_EXPECTED_VAR_NAME_MAKE, peek()));
        }
        final var varNameToken = advance();
        final var varName      = registerVariable(varNameToken);
        final var value        = parseExpr();
        return new Node.MakeStatement(makeToken, varName, value);
    }

    // REPEAT n [ body ]

    private Node.RepeatStatement parseRepeat() {
        final var repeatToken = consume(TokenType.REPEAT);
        final var count       = parseExpr();
        final var body        = parseBlock();
        return new Node.RepeatStatement(repeatToken, count, body);
    }

    // IF cond [ then ]   /   IFELSE cond [ then ] [ else ]

    private Node.IfStatement parseIf(final boolean isElse) {
        final var ifToken   = advance();
        final var condition = parseExpr();
        final var thenBody  = parseBlock();
        final var elseBody  = isElse ? parseBlock() : Collections.<Node>emptyList();
        return new Node.IfStatement(ifToken, condition, thenBody, elseBody);
    }

    // FOR [ var start end [step] ] [ body ]

    private Node.ForStatement parseFor() {
        final var forToken = consume(TokenType.FOR);
        expect(TokenType.LBRACKET, ERR_EXPECTED_LBRACKET_FOR);
        skipNewlines();

        if (!check(TokenType.VARIABLE) && !check(TokenType.IDENTIFIER)) {
            throw new ParseException(ParseError.error(ERR_EXPECTED_VAR_NAME_FOR, peek()));
        }
        final var varToken = advance();
        final var varName  = varToken.value().toLowerCase();
        variableDefinitions.putIfAbsent(varName, varToken);
        final var start   = parseExpr();
        final var end     = parseExpr();
        final var step    = check(TokenType.RBRACKET) ? null : parseExpr();
        expect(TokenType.RBRACKET, ERR_EXPECTED_RBRACKET_FOR);
        skipNewlines();
        final var body = parseBlock();
        return new Node.ForStatement(forToken, varName, start, end, step, body);
    }

    // OUTPUT expr  /  STOP

    private Node.OutputStatement parseOutput() {
        final var t     = consume(TokenType.OUTPUT);
        final var value = parseExpr();
        return new Node.OutputStatement(t, value);
    }

    private Node.OutputStatement parseStop() {
        final var t = consume(TokenType.STOP);
        return new Node.OutputStatement(t, null);
    }

    // WHILE cond [body]  /  UNTIL cond [body]

    private Node parseWhile() {
        final var token     = advance();
        final var condition = parseExpr();
        final var body      = parseBlock();
        return new Node.IfStatement(token, condition, body, Collections.emptyList());
    }

    // TEST cond

    private Node parseTest() {
        final var token     = advance();
        final var condition = parseExpr();
        return new Node.CommandCall(token, List.of(condition));
    }

    // IFTRUE [body]  /  IFFALSE [body]

    private Node parseIfTrueOrFalse(final boolean isFalse) {
        final var token    = advance();
        final var body     = parseBlock();
        final var condTok  = new Token(TokenType.BOOLEAN, isFalse ? "false" : "true",
                token.line(), token.startCol(), token.endCol());
        return new Node.IfStatement(token, new Node.BooleanLiteral(condTok), body, Collections.emptyList());
    }

    // LOCAL "varname

    private Node parseLocal() {
        final var token = consume(TokenType.LOCAL);
        if (!check(TokenType.STRING) && !check(TokenType.IDENTIFIER)) {
            throw new ParseException(ParseError.error(ERR_EXPECTED_VAR_NAME_LOCAL, peek()));
        }
        registerVariable(advance());
        return new Node.CommandCall(token, Collections.emptyList());
    }

    // LOCALMAKE "varname expr

    private Node parseLocalMake() {
        final var token = consume(TokenType.LOCALMAKE);
        if (!check(TokenType.STRING) && !check(TokenType.IDENTIFIER)) {
            throw new ParseException(ParseError.error(ERR_EXPECTED_VAR_NAME_LOCALMAKE, peek()));
        }
        final var varName = registerVariable(advance());
        final var value   = parseExpr();
        return new Node.MakeStatement(token, varName, value);
    }

    // NAME expr "varname  (reversed make)

    private Node parseName() {
        final var token = consume(TokenType.NAME);
        final var value = parseExpr();
        if (!check(TokenType.STRING) && !check(TokenType.IDENTIFIER)) {
            throw new ParseException(ParseError.error(ERR_EXPECTED_VAR_NAME_NAME, peek()));
        }
        registerVariable(advance());
        return new Node.CommandCall(token, List.of(value));
    }

    // DEFINE "procname [[params...][body...]]

    private Node parseDefine() {
        final var token = advance();
        if (!check(TokenType.STRING) && !check(TokenType.IDENTIFIER)) {
            throw new ParseException(ParseError.error(ERR_EXPECTED_PROC_NAME_DEFINE, peek()));
        }
        final var nameToken = advance();
        final var name      = nameToken.value().toLowerCase();
        procedureDefinitions.put(name, nameToken);

        // consume outer [  of  [[params][body]]
        skipNewlines();
        expect(TokenType.LBRACKET, ERR_EXPECTED_DEFINE_SPEC);

        // parse [param ...] as a list literal ( each element is an IDENTIFIER )
        skipNewlines();
        final var paramsList = parsePrimary();
        int arity = 0;
        final var paramNames = new ArrayList<String>();
        if (paramsList instanceof Node.ListLiteral params) {
            arity = params.elements.size();
            for (final Node elem : params.elements) {
                if (elem instanceof Node.CommandCall call) {
                    paramNames.add(call.name);
                    variableDefinitions.putIfAbsent(call.name, call.token);
                }
            }
        }
        procedureArities.put(name, arity);
        procedureParams.put(name, Collections.unmodifiableList(paramNames));

        // parse [body] properly as statements so variable definitions are registered
        skipNewlines();
        final var body = parseBlock();

        // close outer ]
        expect(TokenType.RBRACKET, ERR_EXPECTED_RBRACKET_DEFINE);
        return new Node.ProcedureDef(token, nameToken, Collections.emptyList(), body, null);
    }

    // DOTIMES [varname count] [body]

    private Node parseDotimes() {
        final var token = consume(TokenType.DOTIMES);
        expect(TokenType.LBRACKET, ERR_EXPECTED_LBRACKET_DOTIMES);
        skipNewlines();
        if (!check(TokenType.VARIABLE) && !check(TokenType.IDENTIFIER)) {
            throw new ParseException(ParseError.error(ERR_EXPECTED_VAR_NAME_DOTIMES, peek()));
        }
        final var varToken = advance();
        variableDefinitions.putIfAbsent(varToken.value().toLowerCase(), varToken);
        final var count = parseExpr();
        expect(TokenType.RBRACKET, ERR_EXPECTED_RBRACKET_DOTIMES);
        skipNewlines();
        final var body     = parseBlock();
        final var startTok = new Token(TokenType.NUMBER, "1",
                token.line(), token.startCol(), token.endCol());
        return new Node.ForStatement(token, varToken.value().toLowerCase(),
                new Node.NumberLiteral(startTok), count, null, body);
    }

    // DO.WHILE [body] expr  /  DO.UNTIL [body] expr

    private Node parseDoWhileOrUntil() {
        final var token     = advance();
        final var body      = parseBlock();
        final var condition = parseExpr();
        return new Node.IfStatement(token, condition, body, Collections.emptyList());
    }

    // FILLED fillcolor [body]

    private Node parseFilled() {
        final var token = advance();
        final var color = parseExpr();
        final var body  = parseBlock();
        return new Node.IfStatement(token, color, body, Collections.emptyList());
    }

    private String registerVariable(final Token varNameToken) {
        final var varName = varNameToken.value().toLowerCase();
        final Token defToken = varNameToken.type() == TokenType.STRING
                ? new Token(TokenType.IDENTIFIER, varNameToken.value(),
                            varNameToken.line(), varNameToken.startCol() + 1, varNameToken.endCol())
                : varNameToken;
        variableDefinitions.putIfAbsent(varName, defToken);
        return varName;
    }

    // arity table for built-in commands

    private int inferArity(final TokenType t) {
        return switch (t) {
            case FORWARD, BACK, LEFT, RIGHT,
                 SETX, SETY, LABEL, SETPENSIZE,
                 SETPOS, SETPENCOLOR,
                 PRINT, SHOW,
                 FIRST, LAST,
                 BUTFIRST, BUTLAST, THING,
                 SETHEADING, TOWARDS, SETLABELHEIGHT, CHANGESHAPE,
                 DEF, WAIT, RANDOM, PICK,
                 WORDP, LISTP, ARRAY, ARRAYP, NUMBERP, EMPTYP -> 1;

            case SETXY,
                 SUM, KEYWORD_MINUS, MODULO, POWER,
                 EQUALP, NOTEQUALP,
                 ITEM, LIST,
                 ARC, ELLIPSE, BEFOREP, SUBSTRINGP -> 2;

            case PENUP, PENDOWN, CLEAN, CLEARSCREEN, HOME,
                 HIDETURTLE, SHOWTURTLE, FILL, STOP,
                 PENCOLOR, POS, XCOR, YCOR, HEADING,
                 WRAP, WINDOW, FENCE,
                 SHOWNP, LABELSIZE, PENDOWNP, PENSIZE,
                 BYE, REPCOUNT, READWORD, READLIST -> 0;

            default -> 0;
        };
    }

    // Block  [ statements ]

    private List<Node> parseBlock() {
        skipNewlines();
        expect(TokenType.LBRACKET, ERR_EXPECTED_LBRACKET);
        skipNewlines();
        final var stmts = new ArrayList<Node>();
        while (true) {
            skipNewlines();
            if (atEnd() || check(TokenType.RBRACKET)) break;
            try {
                final var s = parseStatement();
                if (s != null) stmts.add(s);
            } catch (ParseException e) {
                errors.add(e.error);
                synchronize();
            }
        }
        if (check(TokenType.RBRACKET)) {
            advance();
        } else {
            errors.add(ParseError.error(ERR_MISSING_RBRACKET, peek()));
        }
        return stmts;
    }

    // Expression parsing

    private Node parseExpr() {
        return parseComparison();
    }

    private Node parseComparison() {
        Node left = parseAddSub();
        while (checkAny(TokenType.EQUAL_SIGN, TokenType.LESS, TokenType.GREATER)) {
            final var op    = advance();
            final var right = parseAddSub();
            left = new Node.BinaryExpr(op, left, right);
        }
        return left;
    }

    private Node parseAddSub() {
        Node left = parseMulDiv();
        while (checkAny(TokenType.PLUS, TokenType.MINUS)) {
            final var op    = advance();
            final var right = parseMulDiv();
            left = new Node.BinaryExpr(op, left, right);
        }
        return left;
    }

    private Node parseMulDiv() {
        Node left = parseUnary();
        while (checkAny(TokenType.STAR, TokenType.SLASH, TokenType.CARET)) {
            final var op    = advance();
            final var right = parseUnary();
            left = new Node.BinaryExpr(op, left, right);
        }
        return left;
    }

    private Node parseUnary() {
        if (check(TokenType.MINUS)) {
            final var op = advance();
            return new Node.UnaryExpr(op, parseUnary());
        }
        return parsePrimary();
    }

    private Node parsePrimary() {
        final Token t = peek();
        return switch (t.type()) {
            case NUMBER   -> new Node.NumberLiteral(advance());
            case STRING   -> new Node.WordLiteral(advance());
            case BOOLEAN  -> new Node.BooleanLiteral(advance());
            case VARIABLE -> new Node.VariableRef(advance());
            case LBRACKET -> {
                final var lb    = advance();
                final var elems = new ArrayList<Node>();
                while (!atEnd() && !check(TokenType.RBRACKET)) {
                    elems.add(parseExpr());
                    skipNewlines();
                }
                expect(TokenType.RBRACKET, ERR_EXPECTED_RBRACKET);
                yield new Node.ListLiteral(lb, elems);
            }
            case LPAREN -> {
                advance();
                final var inner = parseExpr();
                // Logo allows (func extra args...) for variadic calls, e.g. (readword [prompt])
                if (inner instanceof Node.CommandCall call
                        && !check(TokenType.RPAREN)
                        && !check(TokenType.NEWLINE)
                        && !atEnd()) {
                    final var args = new ArrayList<>(call.args);
                    while (!check(TokenType.RPAREN) && !check(TokenType.NEWLINE) && !atEnd()) {
                        args.add(parseExpr());
                    }
                    expect(TokenType.RPAREN, ERR_EXPECTED_RPAREN);
                    yield new Node.CommandCall(call.token, args);
                }
                expect(TokenType.RPAREN, ERR_EXPECTED_RPAREN);
                yield inner;
            }
            case IDENTIFIER -> new Node.CommandCall(advance(), Collections.emptyList());
            default -> {
                if (isCommandToken(t.type())) {
                    yield parseBuiltinCall();
                }
                throw new ParseException(ParseError.error(
                        String.format(ERR_UNEXPECTED_EXPR_TOKEN_FMT, t.value()), t));
            }
        };
    }

    private boolean isCommandToken(final TokenType type) {
        return switch (type) {
            case FORWARD, BACK, LEFT, RIGHT,
                 SETX, SETY, SETXY, SETPOS, SETHEADING,
                 HOME, ARC, ELLIPSE,
                 POS, XCOR, YCOR, HEADING, TOWARDS,
                 WRAP, WINDOW, FENCE,
                 PENUP, PENDOWN, PENCOLOR, SETPENCOLOR, SETPENSIZE,
                 CLEAN, CLEARSCREEN, FILL, LABEL, SETLABELHEIGHT, CHANGESHAPE,
                 HIDETURTLE, SHOWTURTLE,
                 SHOWNP, LABELSIZE, PENDOWNP, PENSIZE,
                 PRINT, SHOW, READWORD, READLIST,
                 SUM, KEYWORD_MINUS, MODULO, POWER, RANDOM,
                 EQUALP, NOTEQUALP,
                 LIST, FIRST, LAST, BUTFIRST, BUTLAST, ITEM, PICK,
                 THING, MAKE, OUTPUT, STOP, DEF, BYE, WAIT, REPCOUNT,
                 WORDP, LISTP, ARRAY, ARRAYP, NUMBERP, EMPTYP, BEFOREP, SUBSTRINGP -> true;
            default -> false;
        };
    }

    private void skipNewlines() {
        while (check(TokenType.NEWLINE)) advance();
    }

    private boolean atEnd() {
        return peek().type() == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(pos);
    }

    private Token advance() {
        final Token t = tokens.get(pos);
        if (t.type() != TokenType.EOF) pos++;
        return t;
    }

    private boolean check(final TokenType type) {
        return peek().type() == type;
    }

    private boolean checkAny(final TokenType... types) {
        final TokenType cur = peek().type();
        for (final TokenType t : types) if (cur == t) return true;
        return false;
    }

    private Token consume(final TokenType type) {
        if (!check(type)) {
            throw new ParseException(ParseError.error(
                    String.format(ERR_EXPECTED_TYPE_FMT, type, peek().value()), peek()));
        }
        return advance();
    }

    private Token expect(final TokenType type, final String msg) {
        if (!check(type)) {
            errors.add(ParseError.error(msg, peek()));
            final var cur = peek();
            return new Token(type, "", cur.line(), cur.startCol(), cur.startCol());
        }
        return advance();
    }

    private Token expectProcedureName(final String msg) {
        final Token t = peek();
        if (t.type() == TokenType.IDENTIFIER) return advance();
        throw new ParseException(ParseError.error(msg, t));
    }

    private void synchronize() {
        while (!atEnd()) {
            final TokenType t = peek().type();
            if (t == TokenType.NEWLINE || t == TokenType.END || t == TokenType.TO) {
                skipNewlines();
                return;
            }
            advance();
        }
    }

    static class ParseException extends RuntimeException {
        final ParseError error;

        ParseException(final ParseError e) {
            super(e.message);
            this.error = e;
        }
    }
}
