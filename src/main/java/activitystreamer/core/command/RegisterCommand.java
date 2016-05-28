package activitystreamer.core.command;

import activitystreamer.core.command.transmission.gson.JsonRequired;

public class RegisterCommand implements Command {
  private final String command = "REGISTER";
  @JsonRequired
  private String username;
  @JsonRequired
  private String secret;

  public RegisterCommand(String username, String secret) {
    this.setUsername(username);
    this.setSecret(secret);
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof RegisterCommand &&
        username.equals(((RegisterCommand) obj).getUsername()) &&
        secret.equals(((RegisterCommand) obj).getSecret());
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getSecret() {
    return this.secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }
}
