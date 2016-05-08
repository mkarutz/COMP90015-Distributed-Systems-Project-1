package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.AuthenticationFailCommand;
import activitystreamer.server.services.contracts.ConnectionManager;
import activitystreamer.server.services.contracts.ServerAuthService;
import activitystreamer.server.services.impl.NetworkManagerService;
import activitystreamer.core.command.Command;
import activitystreamer.core.shared.*;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class AuthenticationFailCommandHandlerTest {
    @Test
    public void testConnectionIsClosed() {
        ServerAuthService mockServerAuthService = mock(NetworkManagerService.class);
        when(mockServerAuthService.isAuthenticated(any(Connection.class))).thenReturn(true);
        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);

        AuthenticationFailCommandHandler handler = new AuthenticationFailCommandHandler(mockServerAuthService,mockConnectionManager);

        Command mockCommand = spy(new AuthenticationFailCommand("Incorrect secret."));
        Connection mockConnection = mock(Connection.class);

        handler.handleCommand(mockCommand, mockConnection);

        verify(mockConnectionManager).closeConnection(mockConnection);
    }
}
