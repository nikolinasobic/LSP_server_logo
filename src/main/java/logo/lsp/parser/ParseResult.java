package logo.lsp.parser;

import logo.lsp.ast.Node;
import logo.lsp.lexer.Token;

import java.util.List;
import java.util.Map;

public class ParseResult {
    public final Node.Program program;

    public final List<ParseError> errors;

    public final Map<String, Token> procedureDefinitions;

    public final Map<String, Token> variableDefinitions;

    public ParseResult(Node.Program program,
                       List<ParseError> errors,
                       Map<String, Token> procedureDefinitions, Map<String, Token> variableDefinitions) {
        this.program = program;
        this.errors = errors;
        this.procedureDefinitions = procedureDefinitions;
        this.variableDefinitions = variableDefinitions;
    }

    @Override
    public String toString() {
        return "Program " + program;
    }
}
