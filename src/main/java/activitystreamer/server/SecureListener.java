package activitystreamer.server;

import activitystreamer.core.shared.SSLContextFactory;
import activitystreamer.util.Settings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.net.Socket;

// import java.net.ServerSocket;

public class SecureListener implements Runnable {
  private Logger log = LogManager.getLogger();
  private SSLServerSocket sslServerSocket = null;
  private boolean term = false;
  private int port;

  private IncomingConnectionHandler connectionHandler;

  public SecureListener(IncomingConnectionHandler connectionHandler, int port)
      throws IOException {
    this.port = port;
    try {
      SSLContextFactory sslContextFactory = new SSLContextFactory(Settings.getPrivateKeyStore(), Settings.getPrivatePass(), null, null);

      SSLServerSocketFactory sslserversocketfactory = sslContextFactory.getContext().getServerSocketFactory();
      sslServerSocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(port);
    } catch (Exception e) {
      log.error(e.getMessage());
      System.exit(-1);
    }

    this.connectionHandler = connectionHandler;
  }

  @Override
  public void run() {
    log.info("listening for new connections on " + port);
    while (!term) {
      Socket clientSocket;
      try {
        clientSocket = sslServerSocket.accept();
        connectionHandler.incomingConnection(clientSocket);
      } catch (IOException e) {
        log.info("received exception, shutting down");
        term = true;
      }
    }
  }

  public void setTerm(boolean term) {
    this.term = term;
  }
}
