package activitystreamer.core.command;

public class RegisterSuccessCommand implements ICommand {
    private final String command = "REGISTER_SUCCESS";
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
