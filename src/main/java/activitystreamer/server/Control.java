package activitystreamer.server;

import activitystreamer.core.command.transmission.gson.GsonCommandSerializationAdaptor;
import activitystreamer.core.shared.*;
import activitystreamer.core.commandprocessor.*;
import activitystreamer.server.services.*;
import activitystreamer.server.commandprocessors.*;
import activitystreamer.util.Settings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import activitystreamer.core.command.*;

public class Control implements Runnable, IncomingConnectionHandler {
    private Logger log = LogManager.getLogger();
    private List<Connection> connections = new ArrayList<Connection>();
    private boolean term = false;

    private Listener listener;

    private ConnectionStateService rConnectionStateService;
    private RemoteServerStateService rServerService;
    private UserAuthService rUserAuthService;
    private ServerAuthService rServerAuthService;

    public Control() {
        this.rServerService = new RemoteServerStateService(this);
        this.rConnectionStateService = new ConnectionStateService();
        this.rUserAuthService = new UserAuthService(this.rServerService, rConnectionStateService);
        this.rServerAuthService = new ServerAuthService(rConnectionStateService);
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
                garbageCollectConnections();
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

    // Remove connections with no longer operating threads
    private void garbageCollectConnections() {
        List<Connection> toRemove = new ArrayList<Connection>();
        for (Connection c : connections) {
            if (!c.getIsRunning()) {
                toRemove.add(c);
            }
        }
        for (Connection c : toRemove) {
            //this.rServerService.removeStateByHostAndPort(c.getSocket().getInetAddress(), c.getSocket().getPort());
            connections.remove(c);
        }
    }

    @Override
    public synchronized void incomingConnection(Socket s) throws IOException {
        log.debug("incomming connection: " + Settings.socketAddress(s));
        Connection c = new Connection(s,
                new GsonCommandSerializationAdaptor(),
                new GsonCommandSerializationAdaptor(),
                new MainCommandProcessor(
                        rServerService,
                        rUserAuthService,
                        rServerAuthService,
                        rConnectionStateService
                )
        );
        rConnectionStateService.registerConnection(c);
        connections.add(c);
        new Thread(c).start();
    }

    public synchronized Connection outgoingConnection(Socket s) throws IOException {
        log.debug("outgoing connection: " + Settings.socketAddress(s));
        Connection c = new Connection(s,
                new GsonCommandSerializationAdaptor(),
                new GsonCommandSerializationAdaptor(),
                new MainCommandProcessor(
                        rServerService,
                        rUserAuthService,
                        rServerAuthService,
                        rConnectionStateService
                )
        );
        rConnectionStateService.registerConnection(c);
        rConnectionStateService.setConnectionType(c, ConnectionStateService.ConnectionType.SERVER);
        connections.add(c);
        new Thread(c).start();
        ICommand cmd = new AuthenticateCommand(Settings.getSecret());
        c.pushCommand(cmd);
        return c;
    }

    public void announce() {
        log.debug("Broadcasting announce message.");

        // Obviously instanceof is bad design here, but...
        // FOR DEBUG PURPOSES TO SEE LIST OF CONNECTIONS - should be eventually removed!
        System.out.printf("\n================= CONNECTION STATUS =================\n");
        String tf;
        for (Connection c : connections) {
            CommandProcessor cp = c.getCommandProcessor();
            Socket socket = c.getSocket();
            String rsa = socket.getRemoteSocketAddress().toString();
            String[] rsaSplit = rsa.split("/");
            if (rsaSplit[0].equals("localhost")) {
                tf = "to:  ";
            } else {
                tf = "from:";
            }
            ConnectionStateService.ConnectionType t = this.rConnectionStateService.getConnectionType(c);
            if (t == ConnectionStateService.ConnectionType.UNKNOWN) {
                System.out.printf("Connection " + tf + " UNKNOWN  /" + rsaSplit[1] + "\n");
            } else if (t == ConnectionStateService.ConnectionType.SERVER) {
                System.out.printf("Connection " + tf + " SERVER   /" + rsaSplit[1] + "\n");
            } else {
                System.out.printf("Connection " + tf + " CLIENT   /" + rsaSplit[1] + "\n");
            }
        }
        System.out.printf("=====================================================\n\n");
        this.rServerService.printDebugState();
        System.out.printf("\n");

        for (Connection connection : connections){
            ServerAnnounceCommand cmd = new ServerAnnounceCommand(
                Settings.getId(),
                connections.size(),
                connection.getSocket().getLocalAddress(),
                Settings.getLocalPort()
            );

            if (this.rConnectionStateService.getConnectionType(connection) == ConnectionStateService.ConnectionType.SERVER) {
                connection.pushCommand(cmd);
            }
        }
    }

    public final void setTerm(boolean t) {
        term = t;
    }

    public final List<Connection> getConnections() {
        return connections;
    }

    public int getLoad() {
        return getConnections().size();
    }
}
