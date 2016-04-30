package activitystreamer.tester;

import activitystreamer.core.command.*;

public interface Expectation {
    public boolean compare(Command cmd);
}
