package activitystreamer.core.command;

import activitystreamer.core.command.transmission.gson.JsonRequired;
import com.google.gson.JsonObject;

public class ActivityMessageCommand implements ICommand {
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
    public String filter() {
        if (username == null) {
            return "Activity message command should contain a username";
        }
        if (secret == null) {
            secret = "";
        }
        if (activity == null) {
            return "Activity message command should contain an activity message";
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ActivityMessageCommand &&
            username.equals(((ActivityMessageCommand) obj).getUsername()) &&
            secret.equals(((ActivityMessageCommand) obj).getSecret()) &&
            activity.equals(((ActivityMessageCommand) obj).getActivity());
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
        return activity;
    }

    public void setActivity(JsonObject activity) {
        this.activity = activity;
    }
}
