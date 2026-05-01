package logo.lsp.ast;

public class AstPrinter {

    public static String print(Node node) {
        StringBuilder sb = new StringBuilder();
        print(node, sb, 0);
        return sb.toString();
    }

    private static void print(Node node, StringBuilder sb, int indent) {
        String pad = "  ".repeat(indent);

        if (node instanceof Node.Program p) {
            sb.append(pad).append("Program\n");
            for (Node s : p.statements) {
                print(s, sb, indent + 1);
            }
        }

        else if (node instanceof Node.CommandCall c) {
            sb.append(pad).append("Command: ").append(c.name).append("\n");
        }

        else if (node instanceof Node.NumberLiteral n) {
            sb.append(pad).append("Number: ").append(n.value).append("\n");
        }

        else {
            sb.append(pad).append(node.getClass().getSimpleName()).append("\n");
        }
    }
}