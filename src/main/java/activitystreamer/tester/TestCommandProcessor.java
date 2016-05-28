package activitystreamer.tester;

import activitystreamer.core.command.Command;
import activitystreamer.core.commandprocessor.CommandProcessor;
import activitystreamer.core.shared.Connection;

public class TestCommandProcessor extends CommandProcessor {
  TestControl testControl;

  public TestCommandProcessor(TestControl testControl) {
    this.testControl = testControl;
  }

  @Override
  public void processCommandIncoming(Connection connection, Command command) {
    testControl.response(command);
  }

  @Override
  public void invalidMessage(Connection connection, Command command) {

  }
}
