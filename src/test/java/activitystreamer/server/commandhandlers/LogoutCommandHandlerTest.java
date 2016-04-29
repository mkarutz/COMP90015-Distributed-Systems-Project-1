package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.IUserAuthService;
import activitystreamer.server.services.impl.UserAuthService;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class LogoutCommandHandlerTest {
    @Test
    public void testTheConnectionIsClosed() {
        IUserAuthService mockIUserAuthService = mock(UserAuthService.class);

        LogoutCommandHandler handler = new LogoutCommandHandler(mockIUserAuthService);

        LogoutCommand cmd = new LogoutCommand();
        Connection conn = mock(Connection.class);
        handler.handleCommand(cmd, conn);

        verify(conn).close();
    }

    @Test
    public void testTheConnectionIsLoggedOut() {
        IUserAuthService mockIUserAuthService = mock(UserAuthService.class);

        LogoutCommandHandler handler = new LogoutCommandHandler(mockIUserAuthService);

        LogoutCommand command = new LogoutCommand();
        Connection mockConnection = mock(Connection.class);
        handler.handleCommand(command, mockConnection);

        verify(mockIUserAuthService).logout(same(mockConnection));
        verify(mockConnection).close();
    }
}
