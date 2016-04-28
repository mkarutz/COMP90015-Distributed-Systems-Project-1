package activitystreamer.core.command;

public class AuthenticationFailCommand implements ICommand {
    private String info;

    public AuthenticationFailCommand(String info) {
        this.info = info;
    }

    @Override
    public String filter() {
        if (info == null) {
            return "Authentication fail command should contain an info field";
        }
        return null;
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
