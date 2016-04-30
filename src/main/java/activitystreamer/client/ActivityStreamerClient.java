package activitystreamer.client;

import activitystreamer.client.commandprocessors.ServerCommandProcessor;
import activitystreamer.core.command.Command;
import activitystreamer.core.command.LoginCommand;
import activitystreamer.core.command.RegisterCommand;
import activitystreamer.core.command.transmission.gson.GsonCommandSerializationAdaptor;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.commandprocessors.MainCommandProcessor;
import activitystreamer.server.services.impl.ConcreteRemoteServerStateService;
import activitystreamer.util.Settings;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ActivityStreamerClient extends Thread {
//    private final Logger log = LogManager.getLogger();
//    private final List<ActivityListener> listeners = new ArrayList<>();
//
//    private Connection connection;
//    private boolean term = false;
//
//    public ActivityStreamerClient() {
//        this.start();
//    }
//
//    @Override
//    public void run() {
//        initiateConnection();
//        while (!term) {
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                log.info("received an interrupt, system is shutting down");
//                break;
//            }
//
//            // Pop any registered JSON activity objects and send to UI
//            JsonObject obj;
//            while ((obj = clientReflectionService.popActivityJSON()) != null) {
//                textFrame.setOutputText(obj);
//            }
//        }
//    }
//
//    private void initiateConnection() {
//        if (Settings.getRemoteHostname() != null) {
//            try {
//                outgoingConnection(new Socket(Settings.getRemoteHostname(), Settings.getRemotePort()));
//            } catch (IOException e) {
//                log.error("failed to make connection to " + Settings.getRemoteHostname() + ":" + Settings.getRemotePort() + " :" + e);
//                System.exit(-1);
//            }
//        }
//    }
//
//    private synchronized Connection outgoingConnection(Socket s) throws IOException {
//        log.debug("outgoing connection: " + Settings.socketAddress(s));
//        connection = new Connection(s,
//                new GsonCommandSerializationAdaptor(),
//                new GsonCommandSerializationAdaptor(),
//                new MainCommandProcessor(new ConcreteRemoteServerStateService(new C))
//        );
//
//        new Thread(connection).start();
//        Command cmd=null;
//        if (!Settings.getUsername().equals("anonymous") && Settings.getSecret().equals("")){
//            Settings.setSecret(Settings.nextSecret());
//            cmd = new RegisterCommand(Settings.getUsername(), Settings.getSecret());
//        }
//        else{
//            cmd = new LoginCommand(Settings.getUsername(), Settings.getSecret());
//        }
//
//        connection.pushCommand(cmd);
//        return connection;
//    }
//
//    public void disconnect() {
//
//    }
//
//    public void sendActivity(JsonObject activity) {
//
//    }
//
//    public void addActivityListener(ActivityListener listener) {
//        listeners.add(listener);
//    }
//
//    public interface ActivityListener {
//        void onActivity(JsonObject activity);
//    }
}
