package activitystreamer.core.command;

import activitystreamer.core.command.transmission.gson.JsonRequired;

public class RegisterFailedCommand implements Command {
  private final String command = "REGISTER_FAILED";
  @JsonRequired
  private String info;
  private String username;
  private String secret;

  public RegisterFailedCommand() {
  }

  public RegisterFailedCommand(String info, String username, String secret) {
    this.info = info;
    this.username = username;
    this.secret = secret;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof RegisterFailedCommand &&
        info.equals(((RegisterFailedCommand) obj).getInfo());
  }

  public String getInfo() {
    return info;
  }

  public void setInfo(String info) {
    this.info = info;
  }

  public String getUsername() {
    return username;
  }

  public String getSecret() {
    return secret;
  }
}
