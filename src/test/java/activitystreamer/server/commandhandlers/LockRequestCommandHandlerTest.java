package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.ServerAuthService;
import activitystreamer.server.services.contracts.UserAuthService;
import activitystreamer.server.services.impl.ConcreteUserAuthService;
import activitystreamer.server.services.contracts.ConnectionManager;
import activitystreamer.server.services.contracts.ServerAuthService;
import activitystreamer.server.services.impl.NetworkManagerService;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class LockRequestCommandHandlerTest {
    @Test
    public void testUsernameMustBeNotNull() {
        ServerAuthService mockServerAuthService = mock(NetworkManagerService.class);
        UserAuthService mockAuthService = mock(ConcreteUserAuthService.class);
        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);

        LockRequestCommandHandler handler = new LockRequestCommandHandler(
                mockAuthService,
                mockServerAuthService,
                mockConnectionManager
        );

        LockRequestCommand mockCommand = mock(LockRequestCommand.class);
        when(mockCommand.getUsername()).thenReturn(null);
        when(mockCommand.getSecret()).thenReturn("password");

        Connection mockConnection = mock(Connection.class);

        handler.handleCommand(mockCommand, mockConnection);

        verify(mockConnection).pushCommand(isA(InvalidMessageCommand.class));
    }

    @Test
    public void testSecretMustBeNotNull() {
        ServerAuthService mockServerAuthService = mock(NetworkManagerService.class);
        UserAuthService mockAuthService = mock(ConcreteUserAuthService.class);
        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);

        LockRequestCommandHandler handler = new LockRequestCommandHandler(
                mockAuthService,
                mockServerAuthService,
                mockConnectionManager
        );

        LockRequestCommand mockCommand = mock(LockRequestCommand.class);
        when(mockCommand.getUsername()).thenReturn("username");
        when(mockCommand.getSecret()).thenReturn(null);

        Connection mockConnection = mock(Connection.class);

        handler.handleCommand(mockCommand, mockConnection);

        verify(mockConnection).pushCommand(isA(InvalidMessageCommand.class));
    }

    @Test
    public void testIfTheServerHasNotAuthenticatedThenSendInvalidMessageCommand() {
        ServerAuthService mockServerAuthService = mock(NetworkManagerService.class);
        UserAuthService mockAuthService = mock(ConcreteUserAuthService.class);
        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);

        LockRequestCommandHandler handler = new LockRequestCommandHandler(
                mockAuthService,
                mockServerAuthService,
                mockConnectionManager
        );

        LockRequestCommand mockCommand = mock(LockRequestCommand.class);
        when(mockCommand.getUsername()).thenReturn("username");
        when(mockCommand.getSecret()).thenReturn(null);

        Connection mockConnection = mock(Connection.class);

        handler.handleCommand(mockCommand, mockConnection);

        verify(mockConnection).pushCommand(isA(InvalidMessageCommand.class));
    }
}
