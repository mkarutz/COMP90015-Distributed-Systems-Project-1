package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.commandprocessor.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.*;

public class LogoutCommandHandler implements ICommandHandler {

    // private ConnectionStateService rConnectionStateService;
    //
    // public LogoutCommandHandler(ConnectionStateService rConnectionStateService){
    //     this.rConnectionStateService=rConnectionStateService;
    // }

    @Override
    public boolean handleCommand(ICommand command, Connection conn) {
        if (command instanceof LogoutCommand) {

            // if (!(this.rConnectionStateService.getConnectionType(conn)==ConnectionStateService.ConnectionType.CLIENT)){
            //     conn.pushCommand(new InvalidMessageCommand("Imposter CLIENT."));
            //     conn.close();
            //     return true;
            // }

            conn.close();
            return true;
        } else {
            return false;
        }
    }
}
