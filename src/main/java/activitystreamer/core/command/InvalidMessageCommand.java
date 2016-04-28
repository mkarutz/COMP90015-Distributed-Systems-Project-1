package activitystreamer.core.command;

public class InvalidMessageCommand implements ICommand {
    private final String command = "INVALID_MESSAGE";
    private String info;

    public InvalidMessageCommand(String info){
        this.info = info;
    }

    @Override
    public String filter() {
        if (info == null) {
            return "Invalid message command should contain an info field";
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof InvalidMessageCommand &&
            info.equals(((InvalidMessageCommand) obj).getInfo());
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
