package activitystreamer.server;

import activitystreamer.core.command.transmission.CommandDeserializer;
import activitystreamer.core.command.transmission.CommandSerializer;
import activitystreamer.core.commandprocessor.CommandProcessor;
import activitystreamer.core.shared.Connection;
import activitystreamer.core.shared.DisconnectHandler;
import com.google.inject.Inject;

import java.io.IOException;
import java.net.Socket;

public class ConnectionFactory {
  private final CommandSerializer commandSerializer;
  private final CommandDeserializer commandDeserializer;
  private final CommandProcessor commandProcessor;
  private final DisconnectHandler disconnectHandler;

  @Inject
  public ConnectionFactory(CommandSerializer commandSerializer,
                           CommandDeserializer commandDeserializer,
                           CommandProcessor commandProcessor,
                           DisconnectHandler disconnectHandler) {
    this.commandSerializer = commandSerializer;
    this.commandDeserializer = commandDeserializer;
    this.commandProcessor = commandProcessor;
    this.disconnectHandler = disconnectHandler;
  }

  public Connection newConnection(Socket socket) throws IOException {
    return new Connection(socket, commandSerializer, commandDeserializer, commandProcessor, disconnectHandler);
  }
}
