package logo.lsp.server;

import logo.lsp.ast.AstPrinter;
import logo.lsp.lexer.Token;
import logo.lsp.lexer.LogoLexer;
import logo.lsp.parser.LogoParser;
import logo.lsp.parser.ParseResult;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;


public class LogoLanguageServerLauncher {

    private static final Logger LOG =
            Logger.getLogger(LogoLanguageServerLauncher.class.getName());


    public static void main(String[] args) {
        Logger.getLogger("").setLevel(Level.WARNING);

        LogoLanguageServer server = new LogoLanguageServer();
        InputStream in  = System.in;
        OutputStream out = System.out;

        var launcher = LSPLauncher.createServerLauncher(server, in, out);
        LanguageClient client = launcher.getRemoteProxy();
        server.connect(client);

        Future<?> listening = launcher.startListening();
        LOG.info("Logo LSP started (stdio)");

        try {
            listening.get(); // block until client disconnects
        } catch (InterruptedException | ExecutionException e) {
            LOG.log(Level.SEVERE, "LSP server terminated", e);
        }
    }

}