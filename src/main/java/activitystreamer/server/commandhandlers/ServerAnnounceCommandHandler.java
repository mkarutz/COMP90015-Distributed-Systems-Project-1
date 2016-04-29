package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.shared.*;
import activitystreamer.server.*;
import activitystreamer.server.services.impl.ConnectionStateService;
import activitystreamer.server.services.impl.RemoteServerStateService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerAnnounceCommandHandler implements ICommandHandler {
    private Logger log = LogManager.getLogger();

    private RemoteServerStateService rServerService;
    private ConnectionStateService rConnectionStateService;

    public ServerAnnounceCommandHandler(RemoteServerStateService rServerService,
                                        ConnectionStateService rConnectionStateService) {
        this.rServerService = rServerService;
        this.rConnectionStateService = rConnectionStateService;
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

            if (rConnectionStateService.getConnectionType(conn) != ConnectionStateService.ConnectionType.SERVER) {
                conn.pushCommand(new InvalidMessageCommand("Not authenticated."));
                conn.close();
                return true;
            }

            this.rServerService.updateState(
                    cmd.getId(),
                    new ServerState(cmd.getHostname(), cmd.getPort(), cmd.getLoad())
            );

            rConnectionStateService.broadcastToServers(command, conn);

            return true;
        } else {
            return false;
        }
    }
}
