package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.UserAuthService;
import activitystreamer.server.services.impl.ConcreteUserAuthService;
import activitystreamer.server.services.contracts.ConnectionManager;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class LogoutCommandHandlerTest {
    @Test
    public void testTheConnectionIsClosed() {
        UserAuthService mockUserAuthService = mock(ConcreteUserAuthService.class);
        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
        LogoutCommandHandler handler = new LogoutCommandHandler(mockUserAuthService,mockConnectionManager);

        LogoutCommand cmd = new LogoutCommand();
        Connection conn = mock(Connection.class);
        handler.handleCommand(cmd, conn);

        verify(mockConnectionManager).closeConnection(conn);
    }

    @Test
    public void testTheConnectionIsLoggedOut() {

        UserAuthService mockUserAuthService = mock(ConcreteUserAuthService.class);
        when(mockUserAuthService.isLoggedIn(any(Connection.class))).thenReturn(true);

        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
        LogoutCommandHandler handler = new LogoutCommandHandler(mockUserAuthService,mockConnectionManager);

        LogoutCommand command = new LogoutCommand();
        Connection mockConnection = mock(Connection.class);
        handler.handleCommand(command, mockConnection);

        verify(mockUserAuthService).logout(same(mockConnection));
        verify(mockConnectionManager).closeConnection(mockConnection);
    }
}
