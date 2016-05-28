package activitystreamer.core.command;

import activitystreamer.core.command.transmission.gson.JsonRequired;
import com.google.gson.JsonObject;

public class ActivityMessageCommand implements Command {
  private final String command = "ACTIVITY_MESSAGE";
  @JsonRequired
  private String username;
  private String secret;
  @JsonRequired
  private JsonObject activity;

  public ActivityMessageCommand(String username, String secret, JsonObject activity) {
    this.username = username;
    this.secret = secret;
    this.activity = activity;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof ActivityMessageCommand &&
        username.equals(((ActivityMessageCommand) obj).getUsername()) &&
        secret.equals(((ActivityMessageCommand) obj).getSecret()) &&
        activity.equals(((ActivityMessageCommand) obj).getActivity());
  }

  @Override
  public String toString() {
    return "Activity Message Command: " + activity.toString();
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public JsonObject getActivity() {
    if (activity == null) {
      return null;
    }
    return activity.getAsJsonObject();
  }

  public void setActivity(JsonObject activity) {
    this.activity = activity;
  }
}
