package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.AuthenticationFailCommand;
import activitystreamer.core.command.Command;
import activitystreamer.core.command.InvalidMessageCommand;
import activitystreamer.core.commandhandler.ICommandHandler;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.ConnectionManager;
import activitystreamer.server.services.contracts.ServerAuthService;
import com.google.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuthenticationFailCommandHandler implements ICommandHandler {
  private final ServerAuthService serverAuthService;
  private final ConnectionManager connectionManager;
  private Logger log = LogManager.getLogger();

  @Inject
  public AuthenticationFailCommandHandler(ServerAuthService serverAuthService,
                                          ConnectionManager connectionManager) {
    this.serverAuthService = serverAuthService;
    this.connectionManager = connectionManager;
  }

  @Override
  public boolean handleCommand(Command command, Connection conn) {
    if (command instanceof AuthenticationFailCommand) {

      if (!serverAuthService.isAuthenticated(conn)) {
        conn.pushCommand(new InvalidMessageCommand("Not authenticated."));
        connectionManager.closeConnection(conn);
      }

      AuthenticationFailCommand cmd = (AuthenticationFailCommand) command;
      log.error("Authentication failed: " + cmd.getInfo());
      connectionManager.closeConnection(conn);
      return true;
    } else {
      return false;
    }
  }
}
