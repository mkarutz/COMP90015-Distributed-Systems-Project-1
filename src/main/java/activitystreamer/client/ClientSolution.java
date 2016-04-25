package activitystreamer.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import activitystreamer.core.command.*;
import activitystreamer.client.commandprocessors.*;
import activitystreamer.core.shared.Connection;

import com.google.gson.JsonObject;
import activitystreamer.util.Settings;
import java.io.IOException;
import java.net.Socket;

public class ClientSolution implements Runnable {
    private static final Logger log = LogManager.getLogger();
    private TextFrame textFrame;
    private boolean term = false;

    private Connection connection;

    public ClientSolution() {
        // open the gui
        log.debug("opening the gui");
        textFrame = new TextFrame();
    }

    // called by the gui when the user clicks "send"
    public void sendActivityObject(JsonObject activityObj) {

    }

    // called by the gui when the user clicks disconnect
    public void disconnect() {
        textFrame.setVisible(false);
    }


    // the client's run method, to receive messages
    @Override
    public void run() {
        initiateConnection();
        while (!term) {
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

    public synchronized Connection outgoingConnection(Socket s) throws IOException {
        log.debug("outgoing connection: " + Settings.socketAddress(s));
        connection = new Connection(s, new ServerCommandProcessor(), null);
        new Thread(connection).start();
        ICommand cmd = new LoginCommand(Settings.getUsername(), Settings.getSecret());
        connection.pushCommand(cmd);
        return connection;
    }
}
