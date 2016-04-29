package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuthenticateCommandHandler implements ICommandHandler {
    private Logger log = LogManager.getLogger();

    private ServerAuthService rServerAuthService;
    private ConnectionStateService rConnectionStateService;

    public AuthenticateCommandHandler(ServerAuthService rServerAuthService, ConnectionStateService rConnectionStateService) {
        this.rServerAuthService = rServerAuthService;
        this.rConnectionStateService=rConnectionStateService;
    }

    @Override
    public boolean handleCommand(ICommand command, Connection conn) {
        if (command instanceof AuthenticateCommand) {

            if (!(this.rConnectionStateService.getConnectionType(conn)==ConnectionStateService.ConnectionType.UNKNOWN)){
                conn.pushCommand(new InvalidMessageCommand(this.rConnectionStateService.getConnectionType(conn)+" connection cannot send an Authenticate message"));
                conn.close();
                return true;
            }

            AuthenticateCommand cmd = (AuthenticateCommand) command;

            if (cmd.getSecret() == null) {
                conn.pushCommand(new InvalidMessageCommand("Secret must be present"));
                conn.close();
                return true;
            }

            if (!rServerAuthService.authenticate(conn, cmd.getSecret())) {
                conn.pushCommand(new AuthenticationFailCommand("Incorrect secret"));
                conn.close();
            }

            return true;
        } else {
            return false;
        }
    }
}
