package activitystreamer.core.command.transmission;

import activitystreamer.core.command.Command;

public interface CommandSerializer {
    String serialize(Command command);
}
