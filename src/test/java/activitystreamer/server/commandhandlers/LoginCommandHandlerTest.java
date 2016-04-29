package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.InvalidMessageCommand;
import activitystreamer.core.command.LoginCommand;
import activitystreamer.core.command.LoginFailedCommand;
import activitystreamer.core.command.LoginSuccessCommand;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.BroadcastService;
import activitystreamer.server.services.contracts.RemoteServerStateService;
import activitystreamer.server.services.contracts.UserAuthService;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class LoginCommandHandlerTest {
    @Test
    public void aUsernameMustBePreset() {
        UserAuthService mockAuthService = mock(activitystreamer.server.services.impl.UserAuthService.class);
        RemoteServerStateService mockRemoteServerStateService = mock(activitystreamer.server.services.impl.RemoteServerStateService.class);

        BroadcastService broadcastService = mock(BroadcastService.class);

        LoginCommandHandler handler = new LoginCommandHandler(mockAuthService, mockRemoteServerStateService);

        LoginCommand cmd = mock(LoginCommand.class);
        when(cmd.getUsername()).thenReturn(null);

        Connection conn = mock(Connection.class);
        handler.handleCommand(cmd, conn);

        verify(conn).pushCommand(isA(InvalidMessageCommand.class));
        verify(conn).close();
    }

    @Test
    public void ifTheUsernameIsAnonymousThenTheUsernameIsIgnored() {
        UserAuthService mockAuthService = mock(activitystreamer.server.services.impl.UserAuthService.class);
        RemoteServerStateService remoteServerStateService = mock(activitystreamer.server.services.impl.RemoteServerStateService.class);

        LoginCommandHandler handler = new LoginCommandHandler(mockAuthService, remoteServerStateService);

        LoginCommand cmd = mock(LoginCommand.class);
        when(cmd.getUsername()).thenReturn("anonymous");

        Connection conn = mock(Connection.class);

        handler.handleCommand(cmd, conn);

        verify(cmd, never()).getSecret();
    }

    @Test
    public void ifTheUserIsRegisteredALoginSuccessCommandIsSent() {
        UserAuthService authService = mock(activitystreamer.server.services.impl.UserAuthService.class);
        when(authService.isUserRegistered(anyString(), anyString())).thenReturn(true);
        when(authService.login(any(Connection.class), anyString(), anyString())).thenReturn(true);

        RemoteServerStateService serverStateService = mock(activitystreamer.server.services.impl.RemoteServerStateService.class);

        BroadcastService mockConnectionStateService = mock(BroadcastService.class);

        LoginCommandHandler handler = new LoginCommandHandler(authService, serverStateService);

        LoginCommand cmd = mock(LoginCommand.class);
        when(cmd.getUsername()).thenReturn("username");
        when(cmd.getSecret()).thenReturn("password");

        Connection conn = mock(Connection.class);

        handler.handleCommand(cmd, conn);
        verify(conn).pushCommand(isA(LoginSuccessCommand.class));
    }

    @Test
    public void ifThereIsAServerToRedirectToThenARedirectCommandIsSent() {
        UserAuthService authService = mock(activitystreamer.server.services.impl.UserAuthService.class);
        when(authService.isUserRegistered(anyString(), anyString())).thenReturn(true);
        when(authService.login(any(Connection.class), anyString(), anyString())).thenReturn(true);

        RemoteServerStateService remoteServerStateService = mock(RemoteServerStateService.class);

        LoginCommandHandler handler = new LoginCommandHandler(authService, remoteServerStateService);

        LoginCommand cmd = mock(LoginCommand.class);
        when(cmd.getUsername()).thenReturn("username");
        when(cmd.getSecret()).thenReturn("password");

        Connection conn = mock(Connection.class);

        handler.handleCommand(cmd, conn);
        verify(remoteServerStateService).loadBalance(conn);
    }

    @Test
    public void ifTheUserIsNotRegisteredALoginFailedCommandIsSent() {
        UserAuthService authService = mock(activitystreamer.server.services.impl.UserAuthService.class);
        when(authService.isUserRegistered(anyString(), anyString())).thenReturn(false);
        when(authService.login(any(Connection.class), anyString(), anyString())).thenReturn(false);

        RemoteServerStateService remoteServerStateService = mock(activitystreamer.server.services.impl.RemoteServerStateService.class);

        BroadcastService mockConnectionStateService = mock(BroadcastService.class);

        LoginCommandHandler handler = new LoginCommandHandler(authService, remoteServerStateService);

        LoginCommand cmd = mock(LoginCommand.class);
        when(cmd.getUsername()).thenReturn("username");
        when(cmd.getSecret()).thenReturn("password");

        Connection conn = mock(Connection.class);

        handler.handleCommand(cmd, conn);
        verify(remoteServerStateService, never()).loadBalance(conn);
        verify(conn).pushCommand(isA(LoginFailedCommand.class));
    }
}
