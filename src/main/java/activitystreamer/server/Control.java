package activitystreamer.server;

import activitystreamer.core.commandprocessor.PendingCommandProcessor;
import activitystreamer.core.commandprocessor.ServerCommandProcessor;
import activitystreamer.util.Settings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.net.*;

import activitystreamer.core.command.*;

public class Control implements Runnable, IncomingConnectionHandler {
    private Logger log = LogManager.getLogger();
    private List<Connection> connections = new ArrayList<Connection>();
    private boolean term = false;

    private Listener listener;

    public Control() {
      try {
        listener = new Listener(this, Settings.getLocalPort());
      } catch (IOException e1) {
        log.fatal("failed to startup a listening thread: " + e1);
        System.exit(-1);
      }
    }

	@Override
    public void run() {
        initiateConnection();
        new Thread(listener).start();
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
        log.info("closing " + connections.size() + " connections");
        for (Connection connection : connections) {
            connection.close();
        }
        listener.setTerm(true);
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

    public synchronized boolean process(Connection con, String msg) {
        return false;
    }

    public synchronized void connectionClosed(Connection con) {
        if (!term) { connections.remove(con); }
    }

    @Override
    public synchronized void incomingConnection(Socket s) throws IOException {
        log.debug("incomming connection: " + Settings.socketAddress(s));
        Connection c = new Connection(s, new PendingCommandProcessor());
        connections.add(c);
        new Thread(c).start();
    }

    public synchronized Connection outgoingConnection(Socket s) throws IOException {
        log.debug("outgoing connection: " + Settings.socketAddress(s));
        Connection c = new Connection(s, new ServerCommandProcessor());
        connections.add(c);
        new Thread(c).start();
        ICommand cmd = new AuthenticateCommand(Settings.getSecret());
        c.pushCommand(cmd);
        return c;
    }

    public void announce() {
        log.debug("Broadcasting announce message.");
        try {
            ServerAnnounceCommand cmd = new ServerAnnounceCommand(
                Settings.getId(),
                connections.size(),
                InetAddress.getByName(Settings.getLocalHostname()),
                Settings.getLocalPort()
            );

            for (Connection connection: connections){
              // connection.pushCommand(cmd);
            }
        } catch (UnknownHostException | SecurityException e) {
            return;
        }
    }

    public final void setTerm(boolean t) {
        term = t;
    }

    public final List<Connection> getConnections() {
        return connections;
    }
}
