package activitystreamer.core.command;

public class LockDeniedCommand implements ICommand {
    private final String command = "LOCK_DENIED";
    private String username;
    private String secret;
}
