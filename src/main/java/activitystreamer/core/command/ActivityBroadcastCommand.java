package activitystreamer.core.command;

import activitystreamer.core.command.transmission.gson.JsonRequired;
import com.google.gson.JsonObject;

public class ActivityBroadcastCommand implements ICommand {
    @JsonRequired
    private JsonObject activity;

    public ActivityBroadcastCommand(JsonObject activity) {
        this.activity = activity;
    }

    @Override
    public String filter() {
        if (activity == null) {
            return "Activity broadcast command should contain an activity message";
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ActivityBroadcastCommand && activity.equals(((ActivityBroadcastCommand) obj).getActivity());
    }

    public JsonObject getActivity() {
        return activity;
    }

    public void setActivity(JsonObject activity) {
        this.activity = activity;
    }
}
