package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.BroadcastService;
import activitystreamer.server.services.contracts.UserAuthService;
import com.google.gson.JsonObject;
import com.google.inject.Inject;

public class ActivityMessageCommandHandler implements ICommandHandler {
    private UserAuthService userAuthService;
    private BroadcastService broadcastService;

    @Inject
    public ActivityMessageCommandHandler(UserAuthService userAuthService,
                                         BroadcastService broadcastService) {
        this.userAuthService = userAuthService;
        this.broadcastService = broadcastService;
    }

    @Override
    public boolean handleCommand(Command command, Connection conn) {
        if (command instanceof ActivityMessageCommand) {
            ActivityMessageCommand cmd = (ActivityMessageCommand) command;

            if (cmd.getUsername() == null) {
                conn.pushCommand(new InvalidMessageCommand("Username must be present."));
                conn.close();
                return true;
            }

            if (!cmd.getUsername().equals(UserAuthService.ANONYMOUS) && cmd.getSecret() == null) {
                conn.pushCommand(new InvalidMessageCommand("Secret must be present."));
                conn.close();
                return true;
            }

            if (cmd.getActivity() == null) {
                conn.pushCommand(new InvalidMessageCommand("Activity must be present"));
                conn.close();
                return true;
            }

            if (!userAuthService.isLoggedIn(conn)) {
                conn.pushCommand(new AuthenticationFailCommand("Not logged in."));
                conn.close();
                return true;
            }

            if (!userAuthService.authorise(conn, cmd.getUsername(), cmd.getSecret())) {
                conn.pushCommand(new AuthenticationFailCommand("Incorrect username or password."));
                conn.close();
                return true;
            }

            JsonObject activity = cmd.getActivity();
            activity.addProperty("authenticated_user", cmd.getUsername());
            broadcastService.broadcastToAll(new ActivityBroadcastCommand(activity), conn);

            return true;
        } else {
            return false;
        }
    }
}
