package activitystreamer.core.command;

import activitystreamer.core.command.transmission.gson.JsonRequired;

public class LoginSuccessCommand implements Command {
  private final String command = "LOGIN_SUCCESS";
  @JsonRequired
  private String info;
  private String username;
  private String secret;

  public LoginSuccessCommand() {
  }

  public LoginSuccessCommand(String info, String username, String secret) {
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
    return obj instanceof LoginSuccessCommand &&
        info.equals(((LoginSuccessCommand) obj).getInfo());
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
