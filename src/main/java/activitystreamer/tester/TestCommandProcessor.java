package activitystreamer.tester;

import java.util.List;
import java.util.ArrayList;
import activitystreamer.core.command.*;
import activitystreamer.core.commandhandler.*;
import activitystreamer.core.commandprocessor.*;
import activitystreamer.core.shared.*;

public class TestCommandProcessor extends CommandProcessor {
    TestControl testControl;

    public TestCommandProcessor(TestControl testControl) {
        this.testControl = testControl;
    }

    @Override
    public void processCommandIncoming(Connection connection, ICommand command) {
        testControl.response(command);
    }
}
