package activitystreamer.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Listener implements Runnable {
    private Logger log = LogManager.getLogger();
    private ServerSocket serverSocket = null;
    private boolean term = false;
    private int port;

	private IncomingConnectionHandler connectionHandler;

    public Listener(IncomingConnectionHandler connectionHandler, int port)
			throws IOException {
        this.port = port;
        serverSocket = new ServerSocket(port);
        this.connectionHandler = connectionHandler;
    }

    @Override
    public void run() {
        log.info("listening for new connections on " + port);
        while (!term) {
            Socket clientSocket;
            try {
                clientSocket = serverSocket.accept();
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
