package logo.lsp.parser;

import logo.lsp.ast.Node;
import logo.lsp.lexer.Token;

import java.util.List;
import java.util.Map;

public record ParseResult(
        Node.Program              program,
        List<ParseError>          errors,
        List<Token>               tokens,
        Map<String, Token>        procedureDefinitions,
        Map<String, List<String>> procedureParams,
        Map<String, Token>        variableDefinitions
) {}
