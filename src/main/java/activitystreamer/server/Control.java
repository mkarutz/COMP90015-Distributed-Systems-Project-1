package activitystreamer.server;

import activitystreamer.core.command.AuthenticateCommand;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.ConnectionManager;
import activitystreamer.server.services.contracts.RemoteServerStateService;
import activitystreamer.util.Settings;
import com.google.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;

public class Control implements Runnable, IncomingConnectionHandler {
    private Logger log = LogManager.getLogger();
    private boolean term = false;

    private Listener listener;

    private ConnectionFactory connectionFactory;
    private RemoteServerStateService remoteServerStateService;
    private ConnectionManager connectionManager;

    @Inject
    public Control(ConnectionFactory connectionFactory,
                   RemoteServerStateService remoteServerStateService,
                   ConnectionManager connectionManager) {
        this.connectionFactory = connectionFactory;
        this.remoteServerStateService = remoteServerStateService;
        this.connectionManager = connectionManager;
    }

    @Override
    public void run() {
        initiateConnection();
        startListener();
        log.info("using activity interval of " + Settings.getActivityInterval() + " milliseconds");
        while (!term) {
            try {
                Thread.sleep(Settings.getActivityInterval());
            } catch (InterruptedException e) {
                log.info("received an interrupt, system is shutting down");
                break;
            }
            if (!term) {
                announce();
            }
        }
        listener.setTerm(true);
    }

    private void startListener() {
        try {
            listener = new Listener(this, Settings.getLocalPort());
            new Thread(listener).start();
        } catch (IOException e1) {
            log.fatal("failed to startup a listening thread: " + e1);
            System.exit(-1);
        }
    }

    public void initiateConnection() {
        if (Settings.getRemoteHostname() != null) {
            try {
                outgoingConnection(new Socket(Settings.getRemoteHostname(), Settings.getRemotePort()));
            } catch (IOException e) {
                log.error("failed to make connection to " + Settings.getRemoteHostname() + ":" + Settings.getRemotePort() + " :" + e);
                System.exit(-1);
            }
        }
    }

    @Override
    public synchronized void incomingConnection(Socket socket) throws IOException {
        log.debug("incomming connection: " + Settings.socketAddress(socket));
        Connection connection = connectionFactory.newConnection(socket);
        connectionManager.addConnection(connection);
        new Thread(connection).start();
    }

    public synchronized void outgoingConnection(Socket s) throws IOException {
        log.debug("outgoing connection: " + Settings.socketAddress(s));
        Connection connection = connectionFactory.newConnection(s);
        connectionManager.addServerConnection(connection);
        connection.pushCommand(new AuthenticateCommand(Settings.getSecret()));
        new Thread(connection).start();
    }

    public void announce() {
        log.debug("Broadcasting announce message.");
        remoteServerStateService.announce();
    }

    public final void setTerm(boolean t) {
        term = t;
    }
}
