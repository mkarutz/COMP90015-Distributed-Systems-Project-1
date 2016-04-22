package activitystreamer.server;

import java.io.IOException;
import java.net.Socket;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ControlSolution extends Control {
    private Logger log = LogManager.getLogger();

    public ControlSolution() {
        super();
        initiateConnection();
        // start();
    }

    @Override
    public Connection incomingConnection(Socket s) throws IOException {
        Connection con = super.incomingConnection(s);
        return con;
    }

    @Override
    public Connection outgoingConnection(Socket s) throws IOException {
        Connection con = super.outgoingConnection(s);
        return con;
    }

    @Override
    public void connectionClosed(Connection con) {
        super.connectionClosed(con);
    }

    @Override
    public synchronized boolean process(Connection con, String msg) {
        return false;
    }

    @Override
    public boolean doActivity() {
        return false;
    }
}
