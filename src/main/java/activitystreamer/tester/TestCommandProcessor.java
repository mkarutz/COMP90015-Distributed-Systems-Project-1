package activitystreamer.tester;

import activitystreamer.core.command.*;
import activitystreamer.core.commandprocessor.*;
import activitystreamer.core.shared.*;

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
