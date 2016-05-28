package activitystreamer.core.command;

import activitystreamer.core.command.transmission.gson.JsonRequired;

public class InvalidMessageCommand implements Command {
  private final String command = "INVALID_MESSAGE";
  @JsonRequired
  private String info;

  public InvalidMessageCommand(String info) {
    this.info = info;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof InvalidMessageCommand &&
        info.equals(((InvalidMessageCommand) obj).getInfo());
  }

  public String getCommand() {
    return command;
  }

  public String getInfo() {
    return info;
  }

  public void setInfo(String info) {
    this.info = info;
  }
}
