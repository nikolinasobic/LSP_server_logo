package logo.lsp.server;

import logo.lsp.lexer.LogoLexer;
import logo.lsp.lexer.Token;
import logo.lsp.parser.LogoParser;
import logo.lsp.parser.ParseResult;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DocumentStore {

    private final Map<String, ParseResult> results = new ConcurrentHashMap<>();
    private final Map<String, String>      sources = new ConcurrentHashMap<>();

    public ParseResult update(String uri, String text){
        sources.put(uri, text);
        LogoLexer lexer = new LogoLexer(text);
        List<Token> tokens = lexer.tokenize();
        LogoParser parser = new LogoParser(tokens);
        ParseResult result = parser.parse();
        results.put(uri, result);
        return result;
    }

    public ParseResult get(String uri){
        return results.get(uri);
    }

    public String getSource(String uri){
        return sources.getOrDefault(uri, "");
    }

    public void remove(String uri){
        results.remove(uri);
        sources.remove(uri);
    }

}
