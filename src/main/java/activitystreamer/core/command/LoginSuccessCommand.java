package activitystreamer.core.command;

public class LoginSuccessCommand implements ICommand {
    private final String command = "LOGIN_SUCCESS";
    private String info;

    public LoginSuccessCommand(String info) {
        this.info = info;
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
