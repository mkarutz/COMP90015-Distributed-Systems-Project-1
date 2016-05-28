package activitystreamer.core.command;

import activitystreamer.core.command.transmission.gson.JsonRequired;

public class AuthenticateCommand implements Command {
  @JsonRequired
  private String secret;

  public AuthenticateCommand(String secret) {
    this.secret = secret;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof AuthenticateCommand && secret.equals(((AuthenticateCommand) obj).getSecret());
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }
}
