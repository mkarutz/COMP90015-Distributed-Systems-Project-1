package activitystreamer.client;

import activitystreamer.client.commandprocessors.ServerCommandProcessor;
import activitystreamer.client.services.ClientReflectionService;
import activitystreamer.core.command.*;
import activitystreamer.core.command.transmission.gson.GsonCommandSerializationAdaptor;
import activitystreamer.core.shared.Connection;
import activitystreamer.core.shared.SSLContextFactory;
import activitystreamer.util.Settings;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.Socket;

public class ClientSolution implements Runnable {
  private static final Logger log = LogManager.getLogger();
  private TextFrame textFrame;
  private boolean term = false;

  private Connection connection;

  private ClientReflectionService clientReflectionService;

  public ClientSolution() {
    clientReflectionService = new ClientReflectionService();
    log.debug("opening the gui");
    textFrame = new TextFrame(this);
  }

  // called by the gui when the user clicks "send"
  public void sendActivityObject(JsonObject activityObj) {
    Command cmd = new ActivityMessageCommand(Settings.getUsername(), Settings.getSecret(), activityObj);
    connection.pushCommand(cmd);
  }

  // called by the gui when the user clicks disconnect
  public void disconnect() {
    Command cmd = new LogoutCommand();
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
      while ((obj = clientReflectionService.popActivityJSON()) != null) {
        textFrame.setOutputText(obj);
      }
    }
  }

  public void initiateConnection() {
    if (Settings.getRemoteHostname() != null) {
      try {
        if (Settings.getIsSecure()) {
          SSLContextFactory sslContextFactory = new SSLContextFactory(null, null, Settings.getPublicKeyStore(), Settings.getPublicPass());

          SSLSocketFactory sslsocketfactory = sslContextFactory.getContext().getSocketFactory();
          SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(Settings.getRemoteHostname(), Settings.getRemotePort());
          outgoingConnection(sslsocket);
        } else {
          outgoingConnection(new Socket(Settings.getRemoteHostname(), Settings.getRemotePort()));
        }
      } catch (IOException e) {
        log.error("failed to make connection to " + Settings.getRemoteHostname() + ":" + Settings.getRemotePort() + " :" + e);
        System.exit(-1);
      } catch (Exception e) {
        log.error("SSL Exception: " + e.getMessage());
        System.exit(-1);
      }
    }
  }

  public synchronized Connection outgoingConnection(Socket s) throws IOException {
    log.debug("outgoing connection: " + Settings.socketAddress(s));
    connection = new Connection(s,
        new GsonCommandSerializationAdaptor(),
        new GsonCommandSerializationAdaptor(),
        new ServerCommandProcessor(clientReflectionService, this),
        new ClientDisconnectionHandler()
    );

    new Thread(connection).start();
    Command cmd = null;
    if (!Settings.getUsername().equals("anonymous") && Settings.getSecret().equals("")) {
      Settings.setSecret(Settings.nextSecret());
      cmd = new RegisterCommand(Settings.getUsername(), Settings.getSecret());
    } else {
      cmd = new LoginCommand(Settings.getUsername(), Settings.getSecret());
    }

    connection.pushCommand(cmd);
    return connection;
  }
}
