package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.UserAuthService;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class LogoutCommandHandlerTest {
    @Test
    public void testTheConnectionIsClosed() {
        UserAuthService mockUserAuthService = mock(activitystreamer.server.services.impl.UserAuthService.class);

        LogoutCommandHandler handler = new LogoutCommandHandler(mockUserAuthService);

        LogoutCommand cmd = new LogoutCommand();
        Connection conn = mock(Connection.class);
        handler.handleCommand(cmd, conn);

        verify(conn).close();
    }

    @Test
    public void testTheConnectionIsLoggedOut() {
        UserAuthService mockUserAuthService = mock(activitystreamer.server.services.impl.UserAuthService.class);

        LogoutCommandHandler handler = new LogoutCommandHandler(mockUserAuthService);

        LogoutCommand command = new LogoutCommand();
        Connection mockConnection = mock(Connection.class);
        handler.handleCommand(command, mockConnection);

        verify(mockUserAuthService).logout(same(mockConnection));
        verify(mockConnection).close();
    }
}
