package activitystreamer.core.command;

public class RegisterFailedCommand implements ICommand {
    private final String command = "REGISTER_FAILED";
    private String info;
}
