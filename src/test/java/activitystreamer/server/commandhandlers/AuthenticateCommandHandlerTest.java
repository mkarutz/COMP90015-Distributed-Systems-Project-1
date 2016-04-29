package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.ConnectionStateService;
import activitystreamer.server.services.RemoteServerStateService;
import activitystreamer.server.services.ServerAuthService;
import activitystreamer.server.services.UserAuthService;
import activitystreamer.util.Settings;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class AuthenticateCommandHandlerTest {
    
    @Test
    public void testIfClientSendInvalid() {
        ConnectionStateService mockConnectionStateService=mock(ConnectionStateService.class);
        when(mockConnectionStateService.getConnectionType(any(Connection.class))).thenReturn(ConnectionStateService.ConnectionType.CLIENT);
        ServerAuthService mockServerAuthService = spy(new ServerAuthService(mockConnectionStateService));

        AuthenticateCommandHandler handler = new AuthenticateCommandHandler(
                mockServerAuthService,
                mockConnectionStateService
        );

        Settings.setSecret("the actual secret");

        AuthenticateCommand mockCommand = mock(AuthenticateCommand.class);
        when(mockCommand.getSecret()).thenReturn("the actual secret");

        Connection mockConnection = mock(Connection.class);
        handler.handleCommand(mockCommand, mockConnection);

        verify(mockConnection).pushCommand(isA(InvalidMessageCommand.class));
        verify(mockConnection).close();
    }

    @Test
    public void testIfServerSendInvalid() {
        ConnectionStateService mockConnectionStateService=mock(ConnectionStateService.class);
        when(mockConnectionStateService.getConnectionType(any(Connection.class))).thenReturn(ConnectionStateService.ConnectionType.SERVER);
        ServerAuthService mockServerAuthService = spy(new ServerAuthService(mockConnectionStateService));

        AuthenticateCommandHandler handler = new AuthenticateCommandHandler(
                mockServerAuthService,
                mockConnectionStateService
        );

        Settings.setSecret("the actual secret");

        AuthenticateCommand mockCommand = mock(AuthenticateCommand.class);
        when(mockCommand.getSecret()).thenReturn("the actual secret");

        Connection mockConnection = mock(Connection.class);
        handler.handleCommand(mockCommand, mockConnection);

        verify(mockConnection).pushCommand(isA(InvalidMessageCommand.class));
        verify(mockConnection).close();
    }

    @Test
    public void testIfThereIsNoSecretThenSendAnInvalidMessageCommand() {
        ServerAuthService mockServerAuthService = mock(ServerAuthService.class);
        ConnectionStateService mockConnectionStateService=mock(ConnectionStateService.class);
        when(mockConnectionStateService.getConnectionType(any(Connection.class))).thenReturn(ConnectionStateService.ConnectionType.UNKNOWN);

        AuthenticateCommandHandler handler = new AuthenticateCommandHandler(
                mockServerAuthService,
                mockConnectionStateService
        );

        AuthenticateCommand mockCommand = mock(AuthenticateCommand.class);
        when(mockCommand.getSecret()).thenReturn(null);

        Connection mockConnection = mock(Connection.class);
        handler.handleCommand(mockCommand, mockConnection);

        verify(mockConnection).pushCommand(isA(InvalidMessageCommand.class));
        verify(mockConnection).close();
    }

    @Test
    public void testIfTheSecretIsIncorrectThenSendAnAuthenticationFailCommand() {
        ServerAuthService mockServerAuthService = mock(ServerAuthService.class);
        ConnectionStateService mockConnectionStateService=mock(ConnectionStateService.class);
        when(mockConnectionStateService.getConnectionType(any(Connection.class))).thenReturn(ConnectionStateService.ConnectionType.UNKNOWN);

        AuthenticateCommandHandler handler = new AuthenticateCommandHandler(
                mockServerAuthService,
                mockConnectionStateService
        );

        Settings.setSecret("the actual secret");

        AuthenticateCommand mockCommand = mock(AuthenticateCommand.class);
        when(mockCommand.getSecret()).thenReturn("incorrent secret");

        Connection mockConnection = mock(Connection.class);
        handler.handleCommand(mockCommand, mockConnection);

        verify(mockConnection).pushCommand(isA(AuthenticationFailCommand.class));
        verify(mockConnection).close();
    }

    @Test
    public void testIfTheSecretIsCorrectThenAuthenticateTheServer() {
        ConnectionStateService mockConnectionStateService=mock(ConnectionStateService.class);
        when(mockConnectionStateService.getConnectionType(any(Connection.class))).thenReturn(ConnectionStateService.ConnectionType.UNKNOWN);
        ServerAuthService mockServerAuthService = spy(new ServerAuthService(mockConnectionStateService));

        AuthenticateCommandHandler handler = new AuthenticateCommandHandler(
                mockServerAuthService,
                mockConnectionStateService
        );

        Settings.setSecret("the actual secret");

        AuthenticateCommand mockCommand = mock(AuthenticateCommand.class);
        when(mockCommand.getSecret()).thenReturn("the actual secret");

        Connection mockConnection = mock(Connection.class);
        handler.handleCommand(mockCommand, mockConnection);

        verify(mockServerAuthService).authenticate(mockConnection, mockCommand.getSecret());
        verify(mockConnectionStateService).setConnectionType(mockConnection, ConnectionStateService.ConnectionType.SERVER);
        verify(mockConnection, never()).pushCommand(any(ICommand.class));
        verify(mockConnection, never()).close();
    }
}
