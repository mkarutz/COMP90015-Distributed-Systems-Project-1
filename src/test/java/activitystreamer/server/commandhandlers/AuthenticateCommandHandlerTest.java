package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.IServerAuthService;
import activitystreamer.server.services.impl.ConnectionStateService;
import activitystreamer.server.services.impl.ServerAuthService;
import activitystreamer.util.Settings;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class AuthenticateCommandHandlerTest {
    @Test
    public void testIfThereIsNoSecretThenSendAnInvalidMessageCommand() {
        IServerAuthService mockIServerAuthService = mock(ServerAuthService.class);

        AuthenticateCommandHandler handler = new AuthenticateCommandHandler(
                mockIServerAuthService
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
        IServerAuthService mockIServerAuthService = mock(ServerAuthService.class);

        AuthenticateCommandHandler handler = new AuthenticateCommandHandler(
                mockIServerAuthService
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
        ConnectionStateService mockConnectionStateService = spy(new ConnectionStateService());
        IServerAuthService mockIServerAuthService = spy(new ServerAuthService(mockConnectionStateService));

        AuthenticateCommandHandler handler = new AuthenticateCommandHandler(
                mockIServerAuthService
        );

        Settings.setSecret("the actual secret");

        AuthenticateCommand mockCommand = mock(AuthenticateCommand.class);
        when(mockCommand.getSecret()).thenReturn("the actual secret");

        Connection mockConnection = mock(Connection.class);
        handler.handleCommand(mockCommand, mockConnection);

        verify(mockIServerAuthService).authenticate(mockConnection, mockCommand.getSecret());
        verify(mockConnectionStateService).setConnectionType(mockConnection, ConnectionStateService.ConnectionType.SERVER);
        verify(mockConnection, never()).pushCommand(any(ICommand.class));
        verify(mockConnection, never()).close();
    }
}
