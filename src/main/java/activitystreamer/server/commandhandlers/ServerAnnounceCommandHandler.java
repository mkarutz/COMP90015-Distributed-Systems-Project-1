package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.commandprocessor.*;
import activitystreamer.core.shared.*;
import activitystreamer.server.*;
import activitystreamer.server.services.*;
import activitystreamer.util.Settings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerAnnounceCommandHandler implements ICommandHandler {
    private Logger log = LogManager.getLogger();

    private RemoteServerStateService rServerService;
    private ConnectionStateService rConnectionStateService;

    public ServerAnnounceCommandHandler(RemoteServerStateService rServerService, ConnectionStateService rConnectionStateService) {
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

            // Rebroadcast out to all servers
            rConnectionStateService.broadcastToServers(command, conn);

            ServerState ss = new ServerState(cmd.getHostname(), cmd.getPort(), cmd.getLoad());
            this.rServerService.updateState(cmd.getId(), ss);
            return true;
        } else {
            return false;
        }
    }
}
