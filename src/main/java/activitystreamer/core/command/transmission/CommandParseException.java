package activitystreamer.core.command.transmission;

public class CommandParseException extends Exception {
  private String message;

  public CommandParseException(String message) {
    this.message = message;
  }

  @Override
  public String getMessage() {
    return message;
  }
}
