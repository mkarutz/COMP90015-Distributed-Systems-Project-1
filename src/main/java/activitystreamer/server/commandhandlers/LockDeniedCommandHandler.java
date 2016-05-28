package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.Command;
import activitystreamer.core.command.LockDeniedCommand;
import activitystreamer.core.command.LockRequestCommand;
import activitystreamer.core.commandhandler.ICommandHandler;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.UserAuthService;
import activitystreamer.util.Settings;
import com.google.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LockDeniedCommandHandler implements ICommandHandler {
  private final UserAuthService userAuthService;
  private Logger log = LogManager.getLogger();

  @Inject
  public LockDeniedCommandHandler(UserAuthService userAuthService) {
    this.userAuthService = userAuthService;
  }

  @Override
  public boolean handleCommand(Command command, Connection conn) {
    if (command instanceof LockDeniedCommand) {
      LockDeniedCommand cmd = (LockDeniedCommand) command;

      String username = cmd.getUsername();

      if (userAuthService.usernameExists(username)) {
        conn.pushCommand(new LockDeniedCommand(username, userAuthService.getSecret(username)));
        conn.pushCommand(new LockRequestCommand(username, userAuthService.getSecret(username), Settings.getId()));
      }

      return true;
    }

    return false;
  }
}
