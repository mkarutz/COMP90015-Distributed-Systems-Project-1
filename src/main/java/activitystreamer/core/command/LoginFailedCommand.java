package activitystreamer.core.command;

import activitystreamer.core.command.transmission.gson.JsonRequired;

public class LoginFailedCommand implements Command {
  private final String command = "LOGIN_FAILED";
  @JsonRequired
  private String info;
  private String username;
  private String secret;

  public LoginFailedCommand() {
  }

  public LoginFailedCommand(String info, String username, String secret) {
    this.info = info;
    this.username = username;
    this.secret = secret;
  }

  public String getUsername() {
    return username;
  }

  public String getSecret() {
    return secret;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof LoginFailedCommand &&
        info.equals(((LoginFailedCommand) obj).getInfo());
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
