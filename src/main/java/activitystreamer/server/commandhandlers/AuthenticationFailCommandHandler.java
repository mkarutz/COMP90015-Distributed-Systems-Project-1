package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.commandprocessor.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.util.Settings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import activitystreamer.server.services.*;

public class AuthenticationFailCommandHandler implements ICommandHandler {
    private Logger log = LogManager.getLogger();

    //AuthenticationFailCommand closes connection anyway so unnecessary to check source
    // private ConnectionStateService rConnectionStateService;

    // public AuthenticationFailCommandHandler(ConnectionStateService rConnectionStateServic){
    //     this.rConnectionStateService=rConnectionStateService;
    // }

    @Override
    public boolean handleCommand(ICommand command, Connection conn) {
        if (command instanceof AuthenticationFailCommand) {

            // if (!(this.rConnectionStateService.getConnectionType(conn)==ConnectionStateService.ConnectionType.SERVER)){
            //     conn.pushCommand(new InvalidMessageCommand("Imposter SERVER."));
            //     conn.close();
            //     return true;
            // }

            AuthenticationFailCommand failCommand = (AuthenticationFailCommand)command;
            log.error("Authentication failed: " + failCommand.getInfo());
            conn.close();
            return true;
        } else {
            return false;
        }
    }
}
