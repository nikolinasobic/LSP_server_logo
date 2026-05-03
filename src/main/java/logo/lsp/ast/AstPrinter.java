package logo.lsp.ast;

public class AstPrinter {

    private static final String INDENT_UNIT   = "  ";
    private static final String LABEL_PROGRAM = "Program";
    private static final String LABEL_COMMAND = "Command: ";
    private static final String LABEL_NUMBER  = "Number: ";

    public static String print(final Node node) {
        final var sb = new StringBuilder();
        print(node, sb, 0);
        return sb.toString();
    }

    private static void print(final Node node, final StringBuilder sb, final int indent) {
        final String pad = INDENT_UNIT.repeat(indent);

        if (node instanceof Node.Program p) {
            sb.append(pad).append(LABEL_PROGRAM).append('\n');
            for (final Node s : p.statements) {
                print(s, sb, indent + 1);
            }
        } else if (node instanceof Node.CommandCall c) {
            sb.append(pad).append(LABEL_COMMAND).append(c.name).append('\n');
        } else if (node instanceof Node.NumberLiteral n) {
            sb.append(pad).append(LABEL_NUMBER).append(n.value).append('\n');
        } else {
            sb.append(pad).append(node.getClass().getSimpleName()).append('\n');
        }
    }
}
