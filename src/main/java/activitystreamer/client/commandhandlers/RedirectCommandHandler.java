package activitystreamer.client.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.ICommandHandler;
import activitystreamer.core.shared.Connection;
import activitystreamer.client.ClientSolution;
import activitystreamer.util.Settings;

public class RedirectCommandHandler implements ICommandHandler {
    private ClientSolution clientSolution;

    public RedirectCommandHandler(ClientSolution clientSolution) {
        this.clientSolution = clientSolution;
    }

    @Override
    public boolean handleCommandIncoming(ICommand command, Connection conn) {
        if (command instanceof RedirectCommand) {
            RedirectCommand redirectCommand = (RedirectCommand) command;

            Settings.setRemoteHostname(redirectCommand.getHostname().getHostName());
            Settings.setRemotePort(redirectCommand.getPort());

            conn.close();
            clientSolution.initiateConnection();

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean handleCommandOutgoing(ICommand command, Connection conn) {
        if (command instanceof ActivityBroadcastCommand) {
            conn.pushCommandDirect(command);

            return true;
        } else {
            return false;
        }
    }
}
