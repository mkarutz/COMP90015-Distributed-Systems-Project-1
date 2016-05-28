package activitystreamer.tester;

import activitystreamer.core.command.Command;

/* Used internally for tester, and exists largely due to my laziness */
public class ChangeConnCommand implements Command {
  public int port;

  public ChangeConnCommand(int port) {
    this.port = port;
  }
}
