package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.IServerAuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuthenticateCommandHandler implements ICommandHandler {
    private Logger log = LogManager.getLogger();

    private IServerAuthService rIServerAuthService;

    public AuthenticateCommandHandler(IServerAuthService rIServerAuthService) {
        this.rIServerAuthService = rIServerAuthService;
    }

    @Override
    public boolean handleCommand(ICommand command, Connection conn) {
        if (command instanceof AuthenticateCommand) {
            AuthenticateCommand cmd = (AuthenticateCommand) command;

            if (cmd.getSecret() == null) {
                conn.pushCommand(new InvalidMessageCommand("Secret must be present"));
                conn.close();
                return true;
            }

            if (!rIServerAuthService.authenticate(conn, cmd.getSecret())) {
                conn.pushCommand(new AuthenticationFailCommand("Incorrect secret"));
                conn.close();
            }

            return true;
        } else {
            return false;
        }
    }
}
