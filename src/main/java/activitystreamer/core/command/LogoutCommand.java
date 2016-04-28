package activitystreamer.core.command;

public class LogoutCommand implements ICommand {
    private final String command = "LOGOUT";

    @Override
    public String filter() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LogoutCommand;
    }
}
