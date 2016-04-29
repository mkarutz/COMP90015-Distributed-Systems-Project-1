package activitystreamer.tester;

import activitystreamer.core.command.*;

/* Used internally for tester, and exists largely due to my laziness */
public class ChangeConnCommand implements ICommand {
    public int port;

    public ChangeConnCommand(int port) {this.port=port;}

    @Override
    public String filter() {return null;}
}
