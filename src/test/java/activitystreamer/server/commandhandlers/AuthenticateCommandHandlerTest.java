package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.AuthenticateCommand;
import activitystreamer.core.command.AuthenticationFailCommand;
import activitystreamer.core.command.ICommand;
import activitystreamer.core.command.InvalidMessageCommand;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.BroadcastService;
import activitystreamer.server.services.contracts.ServerAuthService;
import activitystreamer.util.Settings;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class AuthenticateCommandHandlerTest {
    @Test
    public void testIfThereIsNoSecretThenSendAnInvalidMessageCommand() {
        ServerAuthService mockServerAuthService = mock(activitystreamer.server.services.impl.ServerAuthService.class);

        AuthenticateCommandHandler handler = new AuthenticateCommandHandler(
                mockServerAuthService
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
        ServerAuthService mockServerAuthService = mock(activitystreamer.server.services.impl.ServerAuthService.class);

        AuthenticateCommandHandler handler = new AuthenticateCommandHandler(
                mockServerAuthService
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
        ServerAuthService mockServerAuthService = mock(ServerAuthService.class);
        when(mockServerAuthService.authenticate(any(Connection.class), anyString())).thenReturn(true);

        AuthenticateCommandHandler handler = new AuthenticateCommandHandler(
                mockServerAuthService
        );

        Settings.setSecret("the actual secret");

        AuthenticateCommand mockCommand = mock(AuthenticateCommand.class);
        when(mockCommand.getSecret()).thenReturn("the actual secret");

        Connection mockConnection = mock(Connection.class);
        handler.handleCommand(mockCommand, mockConnection);

        verify(mockServerAuthService).authenticate(mockConnection, mockCommand.getSecret());
        verify(mockConnection, never()).pushCommand(any(ICommand.class));
        verify(mockConnection, never()).close();
    }
}
