package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.BroadcastService;
import activitystreamer.server.services.contracts.ConnectionManager;
import activitystreamer.server.services.contracts.UserAuthService;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import org.apache.logging.log4j.Logger;

import java.util.logging.LogManager;

public class ActivityMessageCommandHandler implements ICommandHandler {
    private Logger log = org.apache.logging.log4j.LogManager.getLogger();

    private final UserAuthService userAuthService;
    private final BroadcastService broadcastService;
    private final ConnectionManager connectionManager;

    @Inject
    public ActivityMessageCommandHandler(UserAuthService userAuthService,
                                         BroadcastService broadcastService,
                                         ConnectionManager connectionManager) {
        this.userAuthService = userAuthService;
        this.broadcastService = broadcastService;
        this.connectionManager = connectionManager;
    }

    @Override
    public boolean handleCommand(Command command, Connection conn) {
        if (command instanceof ActivityMessageCommand) {
            ActivityMessageCommand cmd = (ActivityMessageCommand) command;

            if (cmd.getUsername() == null) {
                log.info("No username. Terminating connection.");
                conn.pushCommand(new InvalidMessageCommand("Username must be present."));
                connectionManager.closeConnection(conn);
                return true;
            }

            if (!cmd.getUsername().equals(UserAuthService.ANONYMOUS) && cmd.getSecret() == null) {
                log.info("Non anonymous username and no secret. Closing connection.");
                conn.pushCommand(new InvalidMessageCommand("Secret must be present."));
                connectionManager.closeConnection(conn);
                return true;
            }

            if (cmd.getActivity() == null) {
                log.info("No activity object. Closing connection.");
                conn.pushCommand(new InvalidMessageCommand("Activity must be present"));
                connectionManager.closeConnection(conn);
                return true;
            }

            if (!userAuthService.isLoggedIn(conn)) {
                log.info("Connection not logged in as user. Closing connection.");
                conn.pushCommand(new AuthenticationFailCommand("Not logged in."));
                connectionManager.closeConnection(conn);
                return true;
            }

            if (!userAuthService.authorise(conn, cmd.getUsername(), cmd.getSecret())) {
                log.info("Username and secret don't match the logged in credentials. Closing connection.");
                conn.pushCommand(new AuthenticationFailCommand("Incorrect username or password."));
                connectionManager.closeConnection(conn);
                return true;
            }

            JsonObject activity = cmd.getActivity();
            activity.addProperty("authenticated_user", cmd.getUsername());
            log.info("Broadcasting activity: " + activity);
            broadcastService.broadcastToAll(new ActivityBroadcastCommand(activity), conn);

            return true;
        } else {
            return false;
        }
    }
}
