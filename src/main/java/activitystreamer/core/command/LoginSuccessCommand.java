package activitystreamer.core.command;

import activitystreamer.core.command.transmission.gson.JsonRequired;

public class LoginSuccessCommand implements ICommand {
    private final String command = "LOGIN_SUCCESS";
    @JsonRequired
    private String info;

    public LoginSuccessCommand(String info) {
        this.info = info;
    }

    @Override
    public String filter() {
        if (info == null) {
            return "Login success command should contain an info field";
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LoginSuccessCommand &&
            info.equals(((LoginSuccessCommand) obj).getInfo());
    }

    public String getCommand() {
        return command;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
