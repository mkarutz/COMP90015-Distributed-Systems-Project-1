package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.ServerState;
import activitystreamer.server.services.RemoteServerStateService;
import activitystreamer.server.services.UserAuthService;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class LogoutCommandHandlerTest {
    @Test
    public void theServerWillCloseTheConnection() {
        LogoutCommandHandler handler = new LogoutCommandHandler();

        LogoutCommand cmd = new LogoutCommand();
        Connection conn = mock(Connection.class);
        handler.handleCommandIncoming(cmd, conn);

        verify(conn).close();
    }
}
