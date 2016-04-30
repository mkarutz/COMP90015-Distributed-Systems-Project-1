package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.Command;
import activitystreamer.core.command.InvalidMessageCommand;
import activitystreamer.core.command.ServerAnnounceCommand;
import activitystreamer.core.commandhandler.ICommandHandler;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.BroadcastService;
import activitystreamer.server.services.contracts.RemoteServerStateService;
import activitystreamer.server.services.contracts.ServerAuthService;
import com.google.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerAnnounceCommandHandler implements ICommandHandler {
    private Logger log = LogManager.getLogger();

    private RemoteServerStateService remoteServerStateService;
    private BroadcastService broadcastService;
    private ServerAuthService serverAuthService;

    @Inject
    public ServerAnnounceCommandHandler(ServerAuthService serverAuthService,
                                        RemoteServerStateService remoteServerStateService,
                                        BroadcastService broadcastService) {
        this.serverAuthService = serverAuthService;
        this.remoteServerStateService = remoteServerStateService;
        this.broadcastService = broadcastService;
    }

    @Override
    public boolean handleCommand(Command command, Connection conn) {
        if (command instanceof ServerAnnounceCommand) {
            ServerAnnounceCommand cmd = (ServerAnnounceCommand) command;

            if (cmd.getId() == null) {
                conn.pushCommand(new InvalidMessageCommand("An ID must be present."));
                conn.close();
                return true;
            }

            if (!serverAuthService.isAuthenticated(conn)) {
                conn.pushCommand(new InvalidMessageCommand("Not authenticated."));
                conn.close();
                return true;
            }

            remoteServerStateService.updateState(
                    cmd.getId(),
                    cmd.getLoad(),
                    cmd.getHostname(),
                    cmd.getPort()
            );

            broadcastService.broadcastToServers(cmd, conn);

            return true;
        } else {
            return false;
        }
    }
}
