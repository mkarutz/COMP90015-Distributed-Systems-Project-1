package activitystreamer.client.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.commandprocessor.*;
import activitystreamer.core.shared.Connection;

import activitystreamer.util.Settings;

public class RegisterSuccessCommandHandler implements ICommandHandler {
    @Override
    public boolean handleCommand(ICommand command, Connection conn) {
        if (command instanceof RegisterSuccessCommand){
            ICommand cmd = new LoginCommand(Settings.getUsername(),Settings.getSecret());
            conn.pushCommand(cmd);
            return true;
        }
        return false;
    }
}
