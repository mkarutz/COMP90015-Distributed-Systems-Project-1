package activitystreamer.tester;

import activitystreamer.core.command.*;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;

public interface Expectation {
    public boolean compare(ICommand cmd);
}
