package activitystreamer.core.command;

public class AuthenticationFailCommand implements ICommand {
    private String info;

    public AuthenticationFailCommand(String info) {
        this.info = info;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AuthenticationFailCommand && info.equals(((AuthenticationFailCommand) obj).getInfo());
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
