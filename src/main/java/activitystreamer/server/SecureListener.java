package activitystreamer.server;

import java.io.IOException;
// import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SecureListener implements Runnable {
    private Logger log = LogManager.getLogger();
    private SSLServerSocket sslServerSocket = null;
    private boolean term = false;
    private int port;

	private IncomingConnectionHandler connectionHandler;

    public SecureListener(IncomingConnectionHandler connectionHandler, int port)
			throws IOException {
        this.port = port;
        SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        sslServerSocket = (SSLServerSocket) sslserversocketfactory.createServerSocket(port);
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
