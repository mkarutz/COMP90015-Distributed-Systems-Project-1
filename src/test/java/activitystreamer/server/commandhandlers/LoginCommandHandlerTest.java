package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.ServerState;
import activitystreamer.server.services.RemoteServerStateService;
import activitystreamer.server.services.UserAuthService;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class LoginCommandHandlerTest {
    @Test
    public void aUsernameMustBePreset() {
        UserAuthService mockAuthService = mock(UserAuthService.class);
        RemoteServerStateService mockRemoteServerStateService = mock(RemoteServerStateService.class);

        LoginCommandHandler handler = new LoginCommandHandler(mockAuthService, mockRemoteServerStateService);

        LoginCommand cmd = mock(LoginCommand.class);
        when(cmd.getUsername()).thenReturn(null);

        Connection conn = mock(Connection.class);
        handler.handleCommandIncoming(cmd, conn);

        verify(conn).pushCommand(isA(InvalidMessageCommand.class));
        verify(conn).close();
    }

    @Test
    public void ifTheUsernameIsAnonymousThenTheUsernameIsIgnored() {
        UserAuthService mockAuthService = mock(UserAuthService.class);
        RemoteServerStateService mockRemoteServerStateService = mock(RemoteServerStateService.class);

        LoginCommandHandler handler = new LoginCommandHandler(mockAuthService, mockRemoteServerStateService);

        LoginCommand cmd = mock(LoginCommand.class);
        when(cmd.getUsername()).thenReturn("anonymous");

        verify(cmd, never()).getSecret();
    }

    @Test
    public void ifTheUserIsRegisteredALoginSuccessCommandIsSent() {
        UserAuthService authService = mock(UserAuthService.class);
        when(authService.isUserRegistered(anyString(), anyString())).thenReturn(true);

        RemoteServerStateService serverStateService = mock(RemoteServerStateService.class);

        LoginCommandHandler handler = new LoginCommandHandler(authService, serverStateService);

        LoginCommand cmd = mock(LoginCommand.class);
        when(cmd.getUsername()).thenReturn("username");
        when(cmd.getSecret()).thenReturn("password");

        Connection conn = mock(Connection.class);

        handler.handleCommandIncoming(cmd, conn);
        verify(conn).pushCommand(isA(LoginSuccessCommand.class));
    }

    @Test
    public void ifThereIsAServerToRedirectToThenARedirectCommandIsSent() {
        UserAuthService authService = mock(UserAuthService.class);
        when(authService.isUserRegistered(anyString(), anyString())).thenReturn(true);

        ServerState mockServer = mock(ServerState.class);

        RemoteServerStateService serverStateService = mock(RemoteServerStateService.class);
        when(serverStateService.getServerToRedirectTo()).thenReturn(mockServer);

        LoginCommandHandler handler = new LoginCommandHandler(authService, serverStateService);

        LoginCommand cmd = mock(LoginCommand.class);
        when(cmd.getUsername()).thenReturn("username");
        when(cmd.getSecret()).thenReturn("password");

        Connection conn = mock(Connection.class);

        handler.handleCommandIncoming(cmd, conn);
        verify(conn).pushCommand(isA(LoginSuccessCommand.class));
        verify(conn).pushCommand(isA(RedirectCommand.class));
    }

    @Test
    public void ifTheUserIsNotRegisteredALoginFailedCommandIsSent() {
        UserAuthService authService = mock(UserAuthService.class);
        when(authService.isUserRegistered(anyString(), anyString())).thenReturn(false);

        RemoteServerStateService serverStateService = mock(RemoteServerStateService.class);

        LoginCommandHandler handler = new LoginCommandHandler(authService, serverStateService);

        LoginCommand cmd = mock(LoginCommand.class);
        when(cmd.getUsername()).thenReturn("username");
        when(cmd.getSecret()).thenReturn("password");

        Connection conn = mock(Connection.class);

        handler.handleCommandIncoming(cmd, conn);
        verify(conn).pushCommand(isA(LoginFailedCommand.class));
    }
}
