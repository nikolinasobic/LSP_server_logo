package logo.lsp.server;

import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogoLanguageServerLauncher {

    private static final Logger LOG =
            Logger.getLogger(LogoLanguageServerLauncher.class.getName());

    public static void main(final String[] args) {
        Logger.getLogger("").setLevel(Level.WARNING);

        final var server       = new LogoLanguageServer();
        final InputStream in   = System.in;
        final OutputStream out = System.out;

        final var launcher          = LSPLauncher.createServerLauncher(server, in, out);
        final LanguageClient client = launcher.getRemoteProxy();
        server.connect(client);

        final Future<?> listening = launcher.startListening();
        LOG.info(LspConstants.MSG_SERVER_STARTED);

        try {
            listening.get();
        } catch (InterruptedException | ExecutionException e) {
            LOG.log(Level.SEVERE, LspConstants.MSG_SERVER_TERMINATED, e);
        }
    }
}
