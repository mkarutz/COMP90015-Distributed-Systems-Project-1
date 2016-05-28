package activitystreamer.tester;

import activitystreamer.core.command.Command;

public interface Expectation {
  public boolean compare(Command cmd);
}
