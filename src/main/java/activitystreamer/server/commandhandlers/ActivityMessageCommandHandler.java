package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.IBroadcastService;
import activitystreamer.server.services.contracts.IUserAuthService;
import com.google.gson.JsonObject;

public class ActivityMessageCommandHandler implements ICommandHandler {
    private IUserAuthService rAuthService;
    private IBroadcastService rIBroadcastService;

    public ActivityMessageCommandHandler(IUserAuthService rAuthService,
                                         IBroadcastService rIBroadcastService) {
        this.rAuthService = rAuthService;
        this.rIBroadcastService = rIBroadcastService;
    }

    @Override
    public boolean handleCommand(ICommand command, Connection conn) {
        if (command instanceof ActivityMessageCommand) {
            ActivityMessageCommand cmd = (ActivityMessageCommand) command;

            if (cmd.getUsername() == null) {
                conn.pushCommand(new InvalidMessageCommand("Username must be present."));
                conn.close();
                return true;
            }

            if (cmd.getActivity() == null) {
                conn.pushCommand(new InvalidMessageCommand("Activity must be present"));
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

            JsonObject activity = cmd.getActivity();
            activity.addProperty("authenticated_user", cmd.getUsername());
            rIBroadcastService.broadcastToAll(new ActivityBroadcastCommand(activity), conn);

            return true;
        } else {
            return false;
        }
    }
}
