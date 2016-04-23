package activitystreamer.core.command;

public class LoginFailedCommand implements ICommand {
    private final String command = "LOGIN_FAILED";
    private String info;

    public LoginFailedCommand(String info) {
        this.info = info;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LoginFailedCommand &&
            info.equals(((LoginFailedCommand) obj).getInfo());
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
