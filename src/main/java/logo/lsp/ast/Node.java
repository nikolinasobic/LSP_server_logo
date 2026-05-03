package logo.lsp.ast;

import logo.lsp.lexer.Token;

import java.util.List;

public abstract class Node {
    public final Token token;

    protected Node(final Token token) {
        this.token = token;
    }

    public static class Program extends Node {
        private static final String TO_STRING_FMT = "Program{statements=%s}";

        public final List<Node> statements;

        public Program(final List<Node> statements, final Token token) {
            super(token);
            this.statements = statements;
        }

        @Override
        public String toString() {
            return String.format(TO_STRING_FMT, statements);
        }
    }

    // TO name [:param ...] \n body \n END

    public static class ProcedureDef extends Node {
        public final String     name;
        public final List<String> params;
        public final List<Node>   body;
        public final Token      nameToken;
        public final Token      endToken;

        public ProcedureDef(final Token toToken, final Token nameToken,
                            final List<String> params, final List<Node> body,
                            final Token endToken) {
            super(toToken);
            this.nameToken = nameToken;
            this.name      = nameToken.value.toLowerCase();
            this.params    = params;
            this.body      = body;
            this.endToken  = endToken;
        }
    }

    // FORWARD 100

    public static class CommandCall extends Node {
        public final String     name;
        public final List<Node> args;

        public CommandCall(final Token nameToken, final List<Node> args) {
            super(nameToken);
            this.name = nameToken.value.toLowerCase();
            this.args = args;
        }
    }

    // MAKE "varname value

    public static class MakeStatement extends Node {
        public final String varName;
        public final Node   value;

        public MakeStatement(final Token makeToken, final String varName, final Node value) {
            super(makeToken);
            this.varName = varName;
            this.value   = value;
        }
    }

    // REPEAT n [body]

    public static class RepeatStatement extends Node {
        public final Node       count;
        public final List<Node> body;

        public RepeatStatement(final Token repeatToken, final Node count, final List<Node> body) {
            super(repeatToken);
            this.count = count;
            this.body  = body;
        }
    }

    // IF condition [thenBody]
    // IFELSE condition [thenBody][elseBody]

    public static class IfStatement extends Node {
        public final Node       condition;
        public final List<Node> thenBody;
        public final List<Node> elseBody;  // empty for plain IF

        public IfStatement(final Token ifToken, final Node condition,
                           final List<Node> thenBody, final List<Node> elseBody) {
            super(ifToken);
            this.condition = condition;
            this.thenBody  = thenBody;
            this.elseBody  = elseBody;
        }
    }

    // FOR [var start end [step]][body]

    public static class ForStatement extends Node {
        public final String     varName;
        public final Node       start;
        public final Node       end;
        public final Node       step;   // null if not specified
        public final List<Node> body;

        public ForStatement(final Token forToken, final String varName,
                            final Node start, final Node end, final Node step,
                            final List<Node> body) {
            super(forToken);
            this.varName = varName;
            this.start   = start;
            this.end     = end;
            this.step    = step;
            this.body    = body;
        }
    }

    // OUTPUT expr / STOP

    public static class OutputStatement extends Node {
        public final Node value;  // null for STOP

        public OutputStatement(final Token token, final Node value) {
            super(token);
            this.value = value;
        }
    }

    // Block [statements]

    public static class Block extends Node {
        public final List<Node> statements;

        public Block(final Token lbracket, final List<Node> statements) {
            super(lbracket);
            this.statements = statements;
        }
    }

    // binary expression

    public static class BinaryExpr extends Node {
        public final Node   left;
        public final String operator;
        public final Node   right;

        public BinaryExpr(final Token opToken, final Node left, final Node right) {
            super(opToken);
            this.operator = opToken.value;
            this.left     = left;
            this.right    = right;
        }
    }

    // unary/prefix expression

    public static class UnaryExpr extends Node {
        public final String operator;
        public final Node   operand;

        public UnaryExpr(final Token opToken, final Node operand) {
            super(opToken);
            this.operator = opToken.value;
            this.operand  = operand;
        }
    }

    // literals

    public static class NumberLiteral extends Node {
        public final double value;

        public NumberLiteral(final Token token) {
            super(token);
            this.value = Double.parseDouble(token.value);
        }
    }

    public static class WordLiteral extends Node {
        public final String value;  // without leading "

        public WordLiteral(final Token token) {
            super(token);
            this.value = token.value;
        }
    }

    public static class BooleanLiteral extends Node {
        public final boolean value;

        public BooleanLiteral(final Token token) {
            super(token);
            this.value = token.value.equalsIgnoreCase("true");
        }
    }

    // variable reference :name

    public static class VariableRef extends Node {
        public final String name;

        public VariableRef(final Token token) {
            super(token);
            this.name = token.value.toLowerCase();
        }
    }

    // list literal [a b c d]

    public static class ListLiteral extends Node {
        public final List<Node> elements;

        public ListLiteral(final Token lbracket, final List<Node> elements) {
            super(lbracket);
            this.elements = elements;
        }
    }
}
