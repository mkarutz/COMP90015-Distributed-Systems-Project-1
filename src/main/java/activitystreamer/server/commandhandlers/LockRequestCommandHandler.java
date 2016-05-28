package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.Command;
import activitystreamer.core.command.InvalidMessageCommand;
import activitystreamer.core.command.LockDeniedCommand;
import activitystreamer.core.command.LockRequestCommand;
import activitystreamer.core.commandhandler.ICommandHandler;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.BroadcastService;
import activitystreamer.server.services.contracts.ConnectionManager;
import activitystreamer.server.services.contracts.ServerAuthService;
import activitystreamer.server.services.contracts.UserAuthService;
import com.google.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

public class LockRequestCommandHandler implements ICommandHandler {
  private final UserAuthService userAuthService;
  private final ServerAuthService serverAuthService;
  private final ConnectionManager connectionManager;
  private final BroadcastService broadcastService;
  private Logger log = LogManager.getLogger();

  @Inject
  public LockRequestCommandHandler(UserAuthService userAuthService,
                                   ServerAuthService serverAuthService,
                                   ConnectionManager connectionManager,
                                   BroadcastService broadcastService) {
    this.userAuthService = userAuthService;
    this.serverAuthService = serverAuthService;
    this.connectionManager = connectionManager;
    this.broadcastService = broadcastService;
  }

  @Override
  public boolean handleCommand(Command command, Connection conn) {
    if (command instanceof LockRequestCommand) {
      LockRequestCommand cmd = (LockRequestCommand) command;

      if (userAuthService.isLoggedIn(conn)) {
        conn.pushCommand(new InvalidMessageCommand("Unexpected register from client."));
        connectionManager.closeConnection(conn);
        return true;
      }

      if (!serverAuthService.isAuthenticated(conn)) {
        conn.pushCommand(new InvalidMessageCommand("Server not authenticated."));
        connectionManager.closeConnection(conn);
        return true;
      }

      if (connectionManager.isParentConnection(conn)) {
        // Exclude the legacy server that originated the REGISTER
        Set<Connection> exclude = new HashSet<>();
        exclude.add(conn);
        exclude.add(userAuthService.getOriginator(cmd.getUsername(), cmd.getSecret()));

        userAuthService.put(cmd.getUsername(), cmd.getSecret());
        broadcastService.broadcastToServersExcludingMany(command, exclude);
        return true;
      }

      if (!userAuthService.register(cmd.getUsername(), cmd.getSecret(), conn)) {
        conn.pushCommand(new LockDeniedCommand(cmd.getUsername(), cmd.getSecret()));
        return true;
      }

      return true;
    } else {
      return false;
    }
  }
}
