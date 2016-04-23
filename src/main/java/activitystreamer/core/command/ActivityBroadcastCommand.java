package activitystreamer.core.command;

import com.google.gson.JsonObject;

public class ActivityBroadcastCommand implements ICommand {
    private final String command = "ACTIVITY_BROADCAST";
    private JsonObject activity;

    public ActivityBroadcastCommand(JsonObject activity) {
        this.activity = activity;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ActivityBroadcastCommand && activity.equals(((ActivityBroadcastCommand) obj).getActivity());
    }

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
