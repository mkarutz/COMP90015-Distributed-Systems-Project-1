package activitystreamer.tester;

import activitystreamer.core.command.*;

public class ExpectCommandType implements Expectation {
    Class cmpType;

    public ExpectCommandType(Class cmpType) {
        this.cmpType = cmpType;
    }

    @Override
    public boolean compare(ICommand cmd) {
        return cmd.getClass() == this.cmpType;
    }
}
