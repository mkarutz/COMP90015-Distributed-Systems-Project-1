package activitystreamer.core.command;

import com.google.gson.JsonObject;

public class ActivityMessageCommand implements ICommand {
    private final String command = "ACTIVITY_MESSAGE";
    private String username;
    private String secret;
    private JsonObject activity;
}
