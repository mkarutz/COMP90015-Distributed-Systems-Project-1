package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.ServerState;
import activitystreamer.server.services.RemoteServerStateService;
import activitystreamer.server.services.UserAuthService;
import org.junit.Test;
import activitystreamer.server.services.ConnectionStateService;

import static org.mockito.Mockito.*;

public class LogoutCommandHandlerTest {
    @Test
    public void theServerWillCloseTheConnection() {
        // ConnectionStateService mockConnectionStateService=mock(ConnectionStateService.class);
        // when(mockConnectionStateService.getConnectionType(any(Connection.class))).thenReturn(ConnectionStateService.ConnectionType.CLIENT);
        LogoutCommandHandler handler = new LogoutCommandHandler();

        LogoutCommand cmd = new LogoutCommand();
        Connection conn = mock(Connection.class);
        handler.handleCommand(cmd, conn);

        verify(conn).close();
    }
}
