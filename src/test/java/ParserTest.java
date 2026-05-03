
import logo.lsp.ast.Node;
import logo.lsp.lexer.LogoLexer;
import logo.lsp.lexer.Token;
import logo.lsp.parser.LogoParser;
import logo.lsp.parser.ParseResult;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class ParserTest {

    private ParseResult parse(String src) {
        List<Token> tokens = new LogoLexer(src).tokenize();
        return new LogoParser(tokens).parse();
    }

    @Test
    void testSimpleForward() {
        ParseResult r = parse("forward 100");
        assertTrue(r.errors().isEmpty(), "No errors expected");
        assertEquals(1, r.program().statements.size());
        Node.CommandCall cmd = (Node.CommandCall) r.program().statements.get(0);
        assertEquals("forward", cmd.name);
        assertEquals(1, cmd.args.size());
    }

    @Test
    void testRepeat() {
        ParseResult r = parse("repeat 4 [ forward 100 right 90 ]");
        assertTrue(r.errors().isEmpty());
        Node.RepeatStatement rep = (Node.RepeatStatement) r.program().statements.get(0);
        assertNotNull(rep.count);
        assertEquals(2, rep.body.size());
    }

    @Test
    void testProcedureDefinition() {
        String src = "to square :n\n  repeat 4 [ forward :n right 90 ]\nend";
        ParseResult r = parse(src);
        assertTrue(r.errors().isEmpty(), r.errors().toString());
        assertTrue(r.procedureDefinitions().containsKey("square"));
        Node.ProcedureDef def = (Node.ProcedureDef) r.program().statements.get(0);
        assertEquals("square", def.name);
        assertEquals(List.of("n"), def.params);
        assertNotNull(def.endToken);
    }

    @Test
    void testMissingEndReportsError() {
        String src = "to square :n\n  forward :n\n"; // no END
        ParseResult r = parse(src);
        assertFalse(r.errors().isEmpty());
        assertTrue(r.errors().get(0).message.contains("END") ||
                        r.errors().get(0).message.contains("end"),
                "Error should mention END");
    }

    @Test
    void testMakeAndVariableRef() {
        ParseResult r = parse("make \"size 50\nforward :size");
        assertTrue(r.errors().isEmpty(), r.errors().toString());
        assertEquals(2, r.program().statements.size());

        Node.MakeStatement make = (Node.MakeStatement) r.program().statements.get(0);
        assertEquals("size", make.varName);

        Node.CommandCall fwd = (Node.CommandCall) r.program().statements.get(1);
        assertEquals("forward", fwd.name);
        Node.VariableRef ref = (Node.VariableRef) fwd.args.get(0);
        assertEquals("size", ref.name);
    }

    @Test
    void testIfElse() {
        ParseResult r = parse("ifelse :x > 0 [ forward 10 ] [ back 10 ]");
        assertTrue(r.errors().isEmpty(), r.errors().toString());
        Node.IfStatement ifs = (Node.IfStatement) r.program().statements.get(0);
        assertFalse(ifs.thenBody.isEmpty());
        assertFalse(ifs.elseBody.isEmpty());
    }

    @Test
    void testNestedProcedures() {
        String src = """
                to triangle :s
                  repeat 3 [ forward :s right 120 ]
                end
                
                to star :s
                  repeat 5 [ triangle :s right 72 ]
                end
                """;
        ParseResult r = parse(src);
        assertTrue(r.errors().isEmpty(), r.errors().toString());
        assertTrue(r.procedureDefinitions().containsKey("triangle"));
        assertTrue(r.procedureDefinitions().containsKey("star"));
    }

    @Test
    void testArithmeticExpr() {
        ParseResult r = parse("forward :x + 10");
        assertTrue(r.errors().isEmpty(), r.errors().toString());
        Node.CommandCall cmd = (Node.CommandCall) r.program().statements.get(0);
        Node.BinaryExpr expr = (Node.BinaryExpr) cmd.args.get(0);
        assertEquals("+", expr.operator);
    }

    // -----------------------------------------------------------------------
    // Unknown / misspelled command tests
    // -----------------------------------------------------------------------

    @Test
    void testMisspelledCommandReportsError() {
        ParseResult r = parse("forwar 10");
        assertFalse(r.errors().isEmpty(), "Expected an error for unknown command 'forwar'");
        String msg = r.errors().get(0).message.toLowerCase();
        assertTrue(msg.contains("forwar"), "Error should mention the bad token");
    }

    @Test
    void testMisspelledCommandSuggestsCorrection() {
        ParseResult r = parse("forwar 10");
        assertFalse(r.errors().isEmpty());
        String msg = r.errors().get(0).message.toLowerCase();
        assertTrue(msg.contains("forward"),
                "Error should suggest 'forward', got: " + msg);
    }

    @Test
    void testAnotherTypoSuggestion() {
        ParseResult r = parse("bakc 50");
        assertFalse(r.errors().isEmpty());
        String msg = r.errors().get(0).message.toLowerCase();
        assertTrue(msg.contains("back") || msg.contains("bakc"),
                "Expected mention of 'back' or 'bakc', got: " + msg);
    }

    @Test
    void testNumberAtStatementLevelIsError() {
        ParseResult r = parse("10 forward 50");
        assertFalse(r.errors().isEmpty(), "Expected error for '10' at statement level");
        assertTrue(r.errors().get(0).message.contains("10"),
                "Error should mention token '10'");
    }

    @Test
    void testCompletelyUnknownCommandNoSuggestion() {
        ParseResult r = parse("xyz 5");
        assertFalse(r.errors().isEmpty(), "Expected error for unknown command 'xyz'");
        assertTrue(r.errors().get(0).message.toLowerCase().contains("xyz"));
    }

    @Test
    void testUnknownCommandInsideRepeat() {
        ParseResult r = parse("repeat 4 [ forwar 100 right 90 ]");
        assertFalse(r.errors().isEmpty(), "Expected error for 'forwar' inside repeat");
        assertTrue(r.errors().stream().anyMatch(e ->
                e.message.toLowerCase().contains("forwar")));
    }

    @Test
    void testKnownUserProcedureCallIsValid() {
        String src = "to square :n\n  repeat 4 [ forward :n right 90 ]\nend\nsquare 50";
        ParseResult r = parse(src);
        assertTrue(r.errors().isEmpty(),
                "Calling a defined procedure should be valid, errors: " + r.errors());
    }

    @Test
    void testParsingContinuesAfterError() {
        ParseResult r = parse("forwar 10\nforward 50");
        assertEquals(1, r.errors().size());
        assertFalse(r.program().statements.isEmpty());
    }

    @Test
    void testMissingArgumentReportsError() {
        ParseResult r = parse("forward");
        assertFalse(r.errors().isEmpty(), "Expected error for missing argument to 'forward'");
    }
}