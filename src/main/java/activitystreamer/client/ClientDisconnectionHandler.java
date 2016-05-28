package activitystreamer.client;

import activitystreamer.core.shared.Connection;
import activitystreamer.core.shared.DisconnectHandler;

public class ClientDisconnectionHandler implements DisconnectHandler {
  @Override
  public void closeConnection(Connection connection) {
    connection.close();
  }
}
