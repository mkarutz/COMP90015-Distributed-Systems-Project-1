package activitystreamer.server;

import java.io.IOException;
import java.net.Socket;

public interface IncomingConnectionHandler {
  void incomingConnection(Socket clientSocket) throws IOException;
}
