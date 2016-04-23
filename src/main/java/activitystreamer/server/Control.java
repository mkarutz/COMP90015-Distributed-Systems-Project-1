package activitystreamer.server;

import activitystreamer.util.Settings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Control implements Runnable {
    private Logger log = LogManager.getLogger();
    private List<Connection> connections = new ArrayList<Connection>();
    private boolean term = false;

    private Listener listener;

//    public Control() {
//        try {
//            listener = new Listener();
//        } catch (IOException e1) {
//            log.fatal("failed to startup a listening thread: " + e1);
//            System.exit(-1);
//        }
//    }

	@Override
    public void run() {
        log.info("using activity interval of " + Settings.getActivityInterval() + " milliseconds");
        while (!term) {
            try {
                Thread.sleep(Settings.getActivityInterval());
            } catch (InterruptedException e) {
                log.info("received an interrupt, system is shutting down");
                break;
            }
            if (!term) {
                log.debug("doing activity");
                term = doActivity();
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

    public synchronized Connection incomingConnection(Socket s) throws IOException {
        log.debug("incomming connection: " + Settings.socketAddress(s));
        Connection c = new Connection(s);
        connections.add(c);
        return c;
    }

    public synchronized Connection outgoingConnection(Socket s) throws IOException {
        log.debug("outgoing connection: " + Settings.socketAddress(s));
        Connection c = new Connection(s);
        connections.add(c);
        return c;
    }

    public boolean doActivity() {
        // Process each connection
        for (Connection connection : connections) {
            connection.process();
        }
        return false;
    }

    public final void setTerm(boolean t) {
        term = t;
    }

    public final List<Connection> getConnections() {
        return connections;
    }
}
