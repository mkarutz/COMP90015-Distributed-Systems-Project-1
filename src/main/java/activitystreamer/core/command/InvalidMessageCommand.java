package activitystreamer.core.command;

public class InvalidMessageCommand implements ICommand {
    private final String command = "INVALID_MESSAGE";
    private String info;

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
