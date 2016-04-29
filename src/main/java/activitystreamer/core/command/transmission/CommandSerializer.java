package activitystreamer.core.command.transmission;

import activitystreamer.core.command.ICommand;

public interface CommandSerializer {
    String serialize(ICommand command);
}
