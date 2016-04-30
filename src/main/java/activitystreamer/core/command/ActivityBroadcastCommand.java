package activitystreamer.core.command;

import activitystreamer.core.command.transmission.gson.JsonRequired;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ActivityBroadcastCommand implements Command {
    @JsonRequired
    private JsonObject activity;

    public ActivityBroadcastCommand(JsonObject activity) {
        this.activity = activity;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ActivityBroadcastCommand && activity.equals(((ActivityBroadcastCommand) obj).getActivity());
    }

    public JsonObject getActivity() {
        return activity.getAsJsonObject();
    }

    public void setActivity(JsonObject activity) {
        this.activity = activity;
    }
}
