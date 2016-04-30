package activitystreamer.core.command;

import activitystreamer.core.command.transmission.gson.JsonRequired;

public class RegisterSuccessCommand implements Command {
    private final String command = "REGISTER_SUCCESS";
    @JsonRequired
    private String info;

    public RegisterSuccessCommand(String info) {
        this.info = info;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RegisterSuccessCommand &&
            info.equals(((RegisterSuccessCommand) obj).getInfo());
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
