package activitystreamer.core.command.transmission;

import activitystreamer.core.command.ICommand;

public interface CommandDeserializer {
    ICommand deserialize(String message) throws CommandParseException;
}
