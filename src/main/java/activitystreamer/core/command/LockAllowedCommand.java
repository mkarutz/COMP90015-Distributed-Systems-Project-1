package activitystreamer.core.command;

public class LockAllowedCommand implements ICommand {
    private final String command = "LOCK_ALLOWED";
    private String username;
    private String secret;
    private String id;
}
