package activitystreamer.server;

import activitystreamer.core.command.transmission.CommandDeserializer;
import activitystreamer.core.command.transmission.CommandSerializer;
import activitystreamer.core.commandprocessor.CommandProcessor;
import activitystreamer.core.shared.Connection;
import com.google.inject.Inject;

import java.io.IOException;
import java.net.Socket;

public class ConnectionFactory {
    private CommandSerializer commandSerializer;
    private CommandDeserializer commandDeserializer;
    private CommandProcessor commandProcessor;

    @Inject
    public ConnectionFactory(CommandSerializer commandSerializer,
                             CommandDeserializer commandDeserializer,
                             CommandProcessor commandProcessor) {
        this.commandSerializer = commandSerializer;
        this.commandDeserializer = commandDeserializer;
        this.commandProcessor = commandProcessor;
    }

    public Connection newConnection(Socket socket) throws IOException {
        return new Connection(socket, commandSerializer, commandDeserializer, commandProcessor);
    }
}
