package activitystreamer.core.commandhandler;

public interface CommandHandler {
    public boolean handleCommand(ICommand command, Connection conn);
}
