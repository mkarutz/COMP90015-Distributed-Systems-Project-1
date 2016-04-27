package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.RemoteServerStateService;
import activitystreamer.server.services.UserAuthService;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class ActivityMessageCommandHandlerTest {
    @Test
    public void testCanPostAsAnonymousWhenLoggedIn() {
        UserAuthService mockAuthService = spy(new UserAuthService(mock(RemoteServerStateService.class)));
        when(mockAuthService.isLoggedIn(any(Connection.class))).thenReturn(true);

        ActivityMessageCommandHandler handler = new ActivityMessageCommandHandler(mockAuthService);

        ActivityMessageCommand cmd = mock(ActivityMessageCommand.class);
        when(cmd.getUsername()).thenReturn("anonymous");

        Connection mockConnection = mock(Connection.class);

        handler.handleCommandIncoming(cmd, mockConnection);

        verify(mockConnection, never()).pushCommand(isA(AuthenticationFailCommand.class));
    }

    @Test
    public void testIfNonAnonymousCredentialsDoNotMatchThenSendAuthenticaionFailCommand() {
        UserAuthService mockAuthService = mock(UserAuthService.class);
        when(mockAuthService.isLoggedIn(any(Connection.class))).thenReturn(true);
        when(mockAuthService.authorise(any(Connection.class), anyString(), anyString())).thenReturn(false);

        ActivityMessageCommandHandler handler = new ActivityMessageCommandHandler(mockAuthService);

        ActivityMessageCommand cmd = mock(ActivityMessageCommand.class);
        when(cmd.getUsername()).thenReturn("username");
        when(cmd.getSecret()).thenReturn("password");

        Connection mockConnection = mock(Connection.class);

        handler.handleCommandIncoming(cmd, mockConnection);

        verify(mockConnection).pushCommand(isA(AuthenticationFailCommand.class));
    }

    @Test
    public void testIfNotLoggedInThenSendAuthenticationFailCommand() {
        UserAuthService mockAuthService = mock(UserAuthService.class);
        when(mockAuthService.isLoggedIn(any(Connection.class))).thenReturn(false);
        when(mockAuthService.authorise(any(Connection.class), anyString(), anyString())).thenReturn(false);

        ActivityMessageCommandHandler handler = new ActivityMessageCommandHandler(mockAuthService);

        ActivityMessageCommand cmd = mock(ActivityMessageCommand.class);
        when(cmd.getUsername()).thenReturn("username");
        when(cmd.getSecret()).thenReturn("password");

        Connection mockConnection = mock(Connection.class);

        handler.handleCommandIncoming(cmd, mockConnection);

        verify(mockConnection).pushCommand(isA(AuthenticationFailCommand.class));
    }

    @Test
    public void testIfAuthorisedThenBroadcastTheActivityObject() {
//        UserAuthService mockAuthService = mock(UserAuthService.class);
//        when(mockAuthService.isLoggedIn(any(Connection.class))).thenReturn(true);
//        when(mockAuthService.authorise(any(Connection.class), anyString(), anyString())).thenReturn(true);
//
//        ActivityMessageCommandHandler handler = new ActivityMessageCommandHandler(mockAuthService);
//
//        ActivityMessageCommand cmd = mock(ActivityMessageCommand.class);
//        when(cmd.getUsername()).thenReturn("username");
//        when(cmd.getSecret()).thenReturn("password");
//
//
    }
}
