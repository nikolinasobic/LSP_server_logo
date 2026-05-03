package logo.lsp.server;

import logo.lsp.lexer.LogoLexer;
import logo.lsp.parser.LogoParser;
import logo.lsp.parser.ParseResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DocumentStore {

    record DocumentState(String source, ParseResult result) {}

    private final Map<String, DocumentState> documents = new ConcurrentHashMap<>();

    public ParseResult update(final String uri, final String text) {
        final var lexer  = new LogoLexer(text);
        final var tokens = lexer.tokenize();
        final var parser = new LogoParser(tokens);
        final var result = parser.parse();
        documents.put(uri, new DocumentState(text, result));
        return result;
    }

    public DocumentState getState(final String uri) {
        return documents.get(uri);
    }

    public void remove(final String uri) {
        documents.remove(uri);
    }
}
