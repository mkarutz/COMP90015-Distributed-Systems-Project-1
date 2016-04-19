package activitystreamer.core.command;

import com.google.gson.JsonObject;

public class ActivityBroadcastCommand {
    private final String command = "ACTIVITY_BROADCAST";
    private JsonObject activity;

    public String getCommand() {
        return command;
    }

    public JsonObject getActivity() {
        return activity;
    }

    public void setActivity(JsonObject activity) {
        this.activity = activity;
    }
}
