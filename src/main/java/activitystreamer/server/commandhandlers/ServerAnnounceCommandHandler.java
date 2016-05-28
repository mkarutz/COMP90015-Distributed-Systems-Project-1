package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.Command;
import activitystreamer.core.command.InvalidMessageCommand;
import activitystreamer.core.command.ServerAnnounceCommand;
import activitystreamer.core.commandhandler.ICommandHandler;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.BroadcastService;
import activitystreamer.server.services.contracts.ConnectionManager;
import activitystreamer.server.services.contracts.RemoteServerStateService;
import activitystreamer.server.services.contracts.ServerAuthService;
import com.google.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerAnnounceCommandHandler implements ICommandHandler {
  private final RemoteServerStateService remoteServerStateService;
  private final BroadcastService broadcastService;
  private final ServerAuthService serverAuthService;
  private final ConnectionManager connectionManager;
  private Logger log = LogManager.getLogger();

  @Inject
  public ServerAnnounceCommandHandler(RemoteServerStateService remoteServerStateService,
                                      BroadcastService broadcastService,
                                      ServerAuthService serverAuthService,
                                      ConnectionManager connectionManager) {
    this.remoteServerStateService = remoteServerStateService;
    this.broadcastService = broadcastService;
    this.serverAuthService = serverAuthService;
    this.connectionManager = connectionManager;
  }

  @Override
  public boolean handleCommand(Command command, Connection conn) {
    if (command instanceof ServerAnnounceCommand) {
      ServerAnnounceCommand cmd = (ServerAnnounceCommand) command;

      if (cmd.getId() == null) {
        conn.pushCommand(new InvalidMessageCommand("An ID must be present."));
        connectionManager.closeConnection(conn);
        return true;
      }

      if (!serverAuthService.isAuthenticated(conn)) {
        conn.pushCommand(new InvalidMessageCommand("Not authenticated."));
        connectionManager.closeConnection(conn);
        return true;
      }

      remoteServerStateService.updateState(
          cmd.getId(),
          cmd.getLoad(),
          cmd.getHostname(),
          cmd.getPort(),
          cmd.getSecureLoad(),
          cmd.getSecureHostname(),
          cmd.getSecurePort(),
          conn
      );

      return true;
    } else {
      return false;
    }
  }
}
