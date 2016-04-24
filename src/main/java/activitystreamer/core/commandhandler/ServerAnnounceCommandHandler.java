package activitystreamer.core.commandhandler;

import activitystreamer.core.command.*;
import activitystreamer.core.commandprocessor.*;
import activitystreamer.server.*;
import activitystreamer.server.services.*;
import activitystreamer.util.Settings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerAnnounceCommandHandler implements ICommandHandler {
    private Logger log = LogManager.getLogger();

    private RemoteServerStateService rServerService;

    public ServerAnnounceCommandHandler(RemoteServerStateService rServerService) {
        this.rServerService = rServerService;
    }

    @Override
    public boolean handleCommand(ICommand command, Connection conn) {
        if (command instanceof ServerAnnounceCommand) {
            ServerAnnounceCommand announceCommand = (ServerAnnounceCommand)command;
            // Rebroadcast out to all servers
            conn.getCommandBroadcaster().broadcastToServers(command, conn);

            ServerState ss = new ServerState(announceCommand.getHostname(), announceCommand.getPort(), announceCommand.getLoad());
            this.rServerService.updateState(announceCommand.getId(), ss);
            return true;
        } else {
            return false;
        }
    }
}
