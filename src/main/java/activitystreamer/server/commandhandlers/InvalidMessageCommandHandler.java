package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.ICommandHandler;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.ConnectionManager;
import com.google.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InvalidMessageCommandHandler implements ICommandHandler {
    private Logger log = LogManager.getLogger();

    private final ConnectionManager connectionManager;

    @Inject
    public InvalidMessageCommandHandler(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public boolean handleCommand(Command command, Connection conn) {
        if (command instanceof InvalidMessageCommand) {
            InvalidMessageCommand cmd = (InvalidMessageCommand) command;
            log.error("Invalid message was sent: " + cmd.getInfo());
            connectionManager.closeConnection(conn);
            return true;
        } else {
            return false;
        }
    }
}
