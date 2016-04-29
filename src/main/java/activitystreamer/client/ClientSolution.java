package activitystreamer.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import activitystreamer.core.command.*;
import activitystreamer.client.commandprocessors.*;
import activitystreamer.client.services.*;
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

    private ClientReflectionService rClientRefService;

    public ClientSolution() {
        rClientRefService = new ClientReflectionService();

        // open the gui
        log.debug("opening the gui");
        textFrame = new TextFrame(this);
    }

    //testing for badActivity Object
    // public void sendBadActivityObject() {
    //     connection.writeBad("{\"command\":\"ACTIVITY_MESSAGE\",\"username\":\"anonymous\",\"secret\":\"\",\"activity\":\"asdasd\"}");
    // }

    // called by the gui when the user clicks "send"
    public void sendActivityObject(JsonObject activityObj) {
        ICommand cmd = new ActivityMessageCommand(Settings.getUsername(), Settings.getSecret(), activityObj);
        connection.pushCommand(cmd);
    }

    // called by the gui when the user clicks disconnect
    public void disconnect() {
        ICommand cmd = new LogoutCommand();
        connection.pushCommand(cmd);
        textFrame.setVisible(false);
    }


    // the client's run method, to receive messages
    @Override
    public void run() {
        initiateConnection();
        while (!term) {

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                log.info("received an interrupt, system is shutting down");
                break;
            }

            // Pop any registered JSON activity objects and send to UI
            JsonObject obj;
            while ((obj = rClientRefService.popActivityJSON()) != null) {
                textFrame.setOutputText(obj);
            }
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
        connection = new Connection(s, new ServerCommandProcessor(rClientRefService,this));
        //connection = new Connection(s, new ServerCommandProcessor(rClientRefService));

        new Thread(connection).start();
        ICommand cmd=null;
        if (!Settings.getUsername().equals("anonymous") && Settings.getSecret().equals("")){
            Settings.setSecret(Settings.nextSecret());
            cmd = new RegisterCommand(Settings.getUsername(), Settings.getSecret());
        }
        else{
            cmd = new LoginCommand(Settings.getUsername(), Settings.getSecret());
        }

        connection.pushCommand(cmd);
        return connection;
    }
}
