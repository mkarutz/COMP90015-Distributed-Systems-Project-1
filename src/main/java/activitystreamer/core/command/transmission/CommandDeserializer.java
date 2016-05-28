package activitystreamer.core.command.transmission;

import activitystreamer.core.command.Command;

public interface CommandDeserializer {
  Command deserialize(String message) throws CommandParseException;
}
