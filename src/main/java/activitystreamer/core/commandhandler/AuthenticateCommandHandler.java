package activitystreamer.core.commandhandler;

import activitystreamer.core.command.*;
import activitystreamer.core.commandprocessor.*;
import activitystreamer.server.Connection;
import activitystreamer.util.Settings;
import activitystreamer.server.services.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuthenticateCommandHandler implements ICommandHandler {
    private Logger log = LogManager.getLogger();

    private RemoteServerStateService rServerService;

    public AuthenticateCommandHandler(RemoteServerStateService rServerService) {
        this.rServerService = rServerService;
    }

    @Override
    public boolean handleCommand(ICommand command, Connection conn) {
        if (command instanceof AuthenticateCommand) {
            AuthenticateCommand authCommand = (AuthenticateCommand)command;
            if (Settings.getSecret().equals(authCommand.getSecret())) {
                /* Incoming server connection authenticated */
                log.info("Authentication for incoming connection successful");
                // Dealing with a server connection
                conn.setCommandProcessor(new ServerCommandProcessor(this.rServerService));
            } else {
                log.error("Authentication for incoming connection failed");
                ICommand authFailCommand = new AuthenticationFailCommand("Secret was incorrect.");
                conn.pushCommand(authFailCommand);
                conn.close();
            }

            return true;
        } else {
            return false;
        }
    }
}
