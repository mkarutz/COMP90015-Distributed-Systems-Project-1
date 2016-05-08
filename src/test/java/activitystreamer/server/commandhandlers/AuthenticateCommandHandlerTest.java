package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.AuthenticateCommand;
import activitystreamer.server.services.contracts.UserAuthService;
import activitystreamer.server.services.impl.ConcreteUserAuthService;
import activitystreamer.server.services.contracts.ConnectionManager;
import activitystreamer.core.command.AuthenticationFailCommand;
import activitystreamer.core.command.Command;
import activitystreamer.core.command.InvalidMessageCommand;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.ServerAuthService;
import activitystreamer.server.services.impl.NetworkManagerService;
import activitystreamer.util.Settings;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class AuthenticateCommandHandlerTest {
    @Test
    public void testIfThereIsNoSecretThenSendAnInvalidMessageCommand() {
        ServerAuthService mockServerAuthService = mock(NetworkManagerService.class);

        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);

        UserAuthService mockAuthService = mock(ConcreteUserAuthService.class);

        AuthenticateCommandHandler handler = new AuthenticateCommandHandler(
                mockAuthService,
                mockServerAuthService,
                mockConnectionManager
        );

        AuthenticateCommand mockCommand = mock(AuthenticateCommand.class);
        when(mockCommand.getSecret()).thenReturn(null);

        Connection mockConnection = mock(Connection.class);
        handler.handleCommand(mockCommand, mockConnection);

        verify(mockConnection).pushCommand(isA(InvalidMessageCommand.class));
        verify(mockConnectionManager).closeConnection(mockConnection);
    }

    @Test
    public void testIfTheSecretIsIncorrectThenSendAnAuthenticationFailCommand() {
        ServerAuthService mockServerAuthService = mock(NetworkManagerService.class);

        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
        UserAuthService mockAuthService = mock(ConcreteUserAuthService.class);

        AuthenticateCommandHandler handler = new AuthenticateCommandHandler(
                mockAuthService,
                mockServerAuthService,
                mockConnectionManager
        );
        Settings.setSecret("the actual secret");

        AuthenticateCommand mockCommand = mock(AuthenticateCommand.class);
        when(mockCommand.getSecret()).thenReturn("incorrent secret");

        Connection mockConnection = mock(Connection.class);
        handler.handleCommand(mockCommand, mockConnection);

        verify(mockConnection).pushCommand(isA(AuthenticationFailCommand.class));
        verify(mockConnectionManager).closeConnection(mockConnection);
    }

    @Test
    public void testIfTheSecretIsCorrectThenAuthenticateTheServer() {
        ServerAuthService mockServerAuthService = mock(NetworkManagerService.class);
        when(mockServerAuthService.authenticate(any(Connection.class), anyString())).thenReturn(true);

        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);

        UserAuthService mockAuthService = mock(ConcreteUserAuthService.class);

        AuthenticateCommandHandler handler = new AuthenticateCommandHandler(
                mockAuthService,
                mockServerAuthService,
                mockConnectionManager
        );

        Settings.setSecret("the actual secret");

        AuthenticateCommand mockCommand = mock(AuthenticateCommand.class);
        when(mockCommand.getSecret()).thenReturn("the actual secret");

        Connection mockConnection = mock(Connection.class);
        handler.handleCommand(mockCommand, mockConnection);

        verify(mockServerAuthService).authenticate(mockConnection, mockCommand.getSecret());
        verify(mockConnection, never()).pushCommand(any(Command.class));
        verify(mockConnectionManager, never()).closeConnection(mockConnection);
    }
}
