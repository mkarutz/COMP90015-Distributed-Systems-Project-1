package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.*;

public class ActivityMessageCommandHandler implements ICommandHandler {
    private UserAuthService rAuthService;

    public ActivityMessageCommandHandler(UserAuthService rAuthService) {
        this.rAuthService = rAuthService;
    }

    @Override
    public boolean handleCommandIncoming(ICommand command, Connection conn) {
        if (command instanceof ActivityMessageCommand) {
            ActivityMessageCommand cmd = (ActivityMessageCommand) command;

            if (cmd.getUsername() == null) {
                conn.pushCommand(new InvalidMessageCommand("Username must be present."));
                conn.close();
                return true;
            }

            if (!rAuthService.isLoggedIn(conn)) {
                conn.pushCommand(new AuthenticationFailCommand("Not logged in."));
                conn.close();
                return true;
            }

            if (!rAuthService.authorise(conn, cmd.getUsername(), cmd.getSecret())) {
                conn.pushCommand(new AuthenticationFailCommand("Incorrect username or password."));
                conn.close();
                return true;
            }


//            conn.getCommandBroadcaster().broadcast(new);
            
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean handleCommandOutgoing(ICommand command, Connection conn) {
        return false;
    }
}
