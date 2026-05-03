package logo.lsp.server;

import logo.lsp.lexer.LogoLexer;
import logo.lsp.parser.LogoParser;
import logo.lsp.parser.ParseResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DocumentStore {

    private final Map<String, ParseResult> results = new ConcurrentHashMap<>();
    private final Map<String, String>      sources = new ConcurrentHashMap<>();

    public ParseResult update(final String uri, final String text) {
        sources.put(uri, text);
        final var lexer  = new LogoLexer(text);
        final var tokens = lexer.tokenize();
        final var parser = new LogoParser(tokens);
        final var result = parser.parse();
        results.put(uri, result);
        return result;
    }

    public ParseResult get(final String uri) {
        return results.get(uri);
    }

    public String getSource(final String uri) {
        return sources.getOrDefault(uri, "");
    }

    public void remove(final String uri) {
        results.remove(uri);
        sources.remove(uri);
    }
}
