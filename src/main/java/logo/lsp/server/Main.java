package logo.lsp.server;

import logo.lsp.ast.AstPrinter;
import logo.lsp.lexer.Token;
import logo.lsp.lexer.LogoLexer;
import logo.lsp.parser.LogoParser;
import logo.lsp.parser.ParseResult;

import java.util.List;


public class Main {
    public static void main(String[] args) {
        String code = """
            FORWARD 10
            RIGHT 90
            penu
        """;

        LogoLexer lexer = new LogoLexer(code);
        List<Token> tokens = lexer.tokenize();

        LogoParser parser = new LogoParser(tokens);
        ParseResult result = parser.parse();

        System.out.println("=== AST ===");
        //System.out.println(result.program.toString());
        System.out.println(AstPrinter.print(result.program));

        System.out.println("\n=== ERRORS ===");
        result.errors.forEach(e ->
                System.out.println(e.message + " at line " + e.line)
        );
    }
}