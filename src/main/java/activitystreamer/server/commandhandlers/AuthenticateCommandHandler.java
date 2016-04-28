package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.commandprocessor.*;
import activitystreamer.server.commandprocessors.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.util.Settings;
import activitystreamer.server.services.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuthenticateCommandHandler implements ICommandHandler {
    private Logger log = LogManager.getLogger();

    private RemoteServerStateService rServerService;
    private UserAuthService rUserAuthService;
    private ServerAuthService rServerAuthService;
    private ConnectionStateService rConnectionStateService;

    public AuthenticateCommandHandler(RemoteServerStateService rServerService,
                                      UserAuthService rUserAuthService,
                                      ServerAuthService rServerAuthService,
                                      ConnectionStateService rConnectionStateService) {
        this.rServerService = rServerService;
        this.rUserAuthService = rUserAuthService;
        this.rServerAuthService = rServerAuthService;
        this.rConnectionStateService = rConnectionStateService;
    }

    @Override
    public boolean handleCommandIncoming(ICommand command, Connection conn) {
        if (command instanceof AuthenticateCommand) {
            AuthenticateCommand authCommand = (AuthenticateCommand)command;
            if (Settings.getSecret().equals(authCommand.getSecret())) {
                /* Incoming server connection authenticated */
                log.info("Authentication for incoming connection successful");
                // Dealing with a server connection
                rConnectionStateService.setConnectionType(conn, ConnectionStateService.ConnectionType.SERVER);
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

    @Override
    public boolean handleCommandOutgoing(ICommand command, Connection conn) {
        if (command instanceof AuthenticateCommand) {
            conn.pushCommandDirect(command);

            return true;
        } else {
            return false;
        }
    }
}
