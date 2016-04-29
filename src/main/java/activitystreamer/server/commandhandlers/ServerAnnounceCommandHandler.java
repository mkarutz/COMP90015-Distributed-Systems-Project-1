package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.shared.*;
import activitystreamer.server.*;
import activitystreamer.server.services.contracts.IBroadcastService;
import activitystreamer.server.services.contracts.IServerAuthService;
import activitystreamer.server.services.impl.RemoteServerStateService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerAnnounceCommandHandler implements ICommandHandler {
    private Logger log = LogManager.getLogger();

    private RemoteServerStateService remoteServerStateService;
    private IBroadcastService broadcastService;
    private IServerAuthService serverAuthService;

    public ServerAnnounceCommandHandler(IServerAuthService serverAuthService,
                                        RemoteServerStateService remoteServerStateService,
                                        IBroadcastService broadcastService) {
        this.serverAuthService = serverAuthService;
        this.remoteServerStateService = remoteServerStateService;
        this.broadcastService = broadcastService;
    }

    @Override
    public boolean handleCommand(ICommand command, Connection conn) {
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
                    new ServerState(cmd.getHostname(), cmd.getPort(), cmd.getLoad())
            );

            broadcastService.broadcastToServers(command, conn);

            return true;
        } else {
            return false;
        }
    }
}
