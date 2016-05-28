package activitystreamer.tester;

import activitystreamer.core.command.Command;

public class ExpectCommandType implements Expectation {
  Class cmpType;

  public ExpectCommandType(Class cmpType) {
    this.cmpType = cmpType;
  }

  @Override
  public boolean compare(Command cmd) {
    return cmd.getClass() == this.cmpType;
  }
}
