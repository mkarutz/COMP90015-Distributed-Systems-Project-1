package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.InvalidMessageCommand;
import activitystreamer.core.command.LoginCommand;
import activitystreamer.core.command.LoginFailedCommand;
import activitystreamer.core.command.LoginSuccessCommand;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.BroadcastService;
import activitystreamer.server.services.contracts.RemoteServerStateService;
import activitystreamer.server.services.contracts.UserAuthService;
import activitystreamer.server.services.impl.ConcreteRemoteServerStateService;
import activitystreamer.server.services.impl.ConcreteUserAuthService;
import activitystreamer.server.services.contracts.ConnectionManager;
import activitystreamer.server.services.contracts.ServerAuthService;
import activitystreamer.server.services.impl.NetworkManagerService;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class LoginCommandHandlerTest {
    @Test
    public void aUsernameMustBePreset() {
        UserAuthService mockAuthService = mock(ConcreteUserAuthService.class);
        ServerAuthService mockServerAuthService = mock(NetworkManagerService.class);
        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
        RemoteServerStateService mockRemoteServerStateService = mock(ConcreteRemoteServerStateService.class);

        BroadcastService broadcastService = mock(BroadcastService.class);

        LoginCommandHandler handler = new LoginCommandHandler(mockAuthService,mockServerAuthService,mockRemoteServerStateService,mockConnectionManager);

        LoginCommand cmd = mock(LoginCommand.class);
        when(cmd.getUsername()).thenReturn(null);

        Connection conn = mock(Connection.class);
        handler.handleCommand(cmd, conn);

        verify(conn).pushCommand(isA(InvalidMessageCommand.class));
        verify(mockConnectionManager).closeConnection(conn);
    }

    @Test
    public void ifTheUsernameIsAnonymousThenTheUsernameIsIgnored() {
        UserAuthService mockAuthService = mock(ConcreteUserAuthService.class);
        ServerAuthService mockServerAuthService = mock(NetworkManagerService.class);
        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
        RemoteServerStateService mockRemoteServerStateService = mock(ConcreteRemoteServerStateService.class);

        BroadcastService broadcastService = mock(BroadcastService.class);

        LoginCommandHandler handler = new LoginCommandHandler(mockAuthService,mockServerAuthService,mockRemoteServerStateService,mockConnectionManager);

        LoginCommand cmd = mock(LoginCommand.class);
        when(cmd.getUsername()).thenReturn("anonymous");

        Connection conn = mock(Connection.class);

        handler.handleCommand(cmd, conn);

        verify(cmd, never()).getSecret();
    }

    @Test
    public void ifTheUserIsRegisteredALoginSuccessCommandIsSent() {
        UserAuthService mockAuthService = mock(ConcreteUserAuthService.class);
        when(mockAuthService.isUserRegistered(anyString(), anyString())).thenReturn(true);
        when(mockAuthService.login(any(Connection.class), anyString(), anyString())).thenReturn(true);

        ServerAuthService mockServerAuthService = mock(NetworkManagerService.class);
        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
        RemoteServerStateService mockRemoteServerStateService = mock(ConcreteRemoteServerStateService.class);

        BroadcastService broadcastService = mock(BroadcastService.class);

        LoginCommandHandler handler = new LoginCommandHandler(mockAuthService,mockServerAuthService,mockRemoteServerStateService,mockConnectionManager);


        LoginCommand cmd = mock(LoginCommand.class);
        when(cmd.getUsername()).thenReturn("username");
        when(cmd.getSecret()).thenReturn("password");

        Connection conn = mock(Connection.class);

        handler.handleCommand(cmd, conn);
        verify(conn).pushCommand(isA(LoginSuccessCommand.class));
    }

    @Test
    public void ifThereIsAServerToRedirectToThenARedirectCommandIsSent() {
        UserAuthService mockAuthService = mock(ConcreteUserAuthService.class);
        when(mockAuthService.isUserRegistered(anyString(), anyString())).thenReturn(true);
        when(mockAuthService.login(any(Connection.class), anyString(), anyString())).thenReturn(true);

        ServerAuthService mockServerAuthService = mock(NetworkManagerService.class);
        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
        RemoteServerStateService mockRemoteServerStateService = mock(ConcreteRemoteServerStateService.class);

        BroadcastService broadcastService = mock(BroadcastService.class);

        LoginCommandHandler handler = new LoginCommandHandler(mockAuthService,mockServerAuthService,mockRemoteServerStateService,mockConnectionManager);

        LoginCommand cmd = mock(LoginCommand.class);
        when(cmd.getUsername()).thenReturn("username");
        when(cmd.getSecret()).thenReturn("password");

        Connection conn = mock(Connection.class);

        handler.handleCommand(cmd, conn);
        verify(mockRemoteServerStateService).loadBalance(conn);
    }

    @Test
    public void ifTheUserIsNotRegisteredALoginFailedCommandIsSent() {
        UserAuthService mockAuthService = mock(ConcreteUserAuthService.class);
        when(mockAuthService.isUserRegistered(anyString(), anyString())).thenReturn(false);
        when(mockAuthService.login(any(Connection.class), anyString(), anyString())).thenReturn(false);

        ServerAuthService mockServerAuthService = mock(NetworkManagerService.class);
        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
        RemoteServerStateService mockRemoteServerStateService = mock(ConcreteRemoteServerStateService.class);

        BroadcastService broadcastService = mock(BroadcastService.class);

        LoginCommandHandler handler = new LoginCommandHandler(mockAuthService,mockServerAuthService,mockRemoteServerStateService,mockConnectionManager);

        LoginCommand cmd = mock(LoginCommand.class);
        when(cmd.getUsername()).thenReturn("username");
        when(cmd.getSecret()).thenReturn("password");

        Connection conn = mock(Connection.class);

        handler.handleCommand(cmd, conn);
        verify(mockRemoteServerStateService, never()).loadBalance(conn);
        verify(conn).pushCommand(isA(LoginFailedCommand.class));
    }
}
