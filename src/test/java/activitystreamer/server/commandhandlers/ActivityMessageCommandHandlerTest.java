package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.ActivityBroadcastCommand;
import activitystreamer.core.command.ActivityMessageCommand;
import activitystreamer.core.command.AuthenticationFailCommand;
import activitystreamer.core.command.ICommandBroadcaster;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.*;
import com.google.gson.JsonObject;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ActivityMessageCommandHandlerTest {
    @Test
    public void testCanPostAsAnonymousWhenLoggedIn() {
        RemoteServerStateService mockServerService = mock(RemoteServerStateService.class);
        ConnectionStateService mockConnectionStateService = mock(ConnectionStateService.class);

        UserAuthService mockAuthService = spy(new UserAuthService(mockServerService, mockConnectionStateService));
        when(mockAuthService.isLoggedIn(any(Connection.class))).thenReturn(true);

        ActivityMessageCommandHandler handler
                = new ActivityMessageCommandHandler(mockAuthService, mockConnectionStateService);

        ActivityMessageCommand mockCommand = mock(ActivityMessageCommand.class);
        when(mockCommand.getUsername()).thenReturn("anonymous");

        JsonObject activity = new JsonObject();
        when(mockCommand.getActivity()).thenReturn(activity);

        Connection mockConnection = mock(Connection.class);

        handler.handleCommandIncoming(mockCommand, mockConnection);

        verify(mockConnection, never()).pushCommand(isA(AuthenticationFailCommand.class));
    }

    @Test
    public void testIfNonAnonymousCredentialsDoNotMatchThenSendAuthenticaionFailCommand() {
        UserAuthService mockAuthService = mock(UserAuthService.class);
        when(mockAuthService.isLoggedIn(any(Connection.class))).thenReturn(true);
        when(mockAuthService
                .authorise(any(Connection.class), anyString(), anyString()))
                .thenReturn(false);

        ConnectionStateService mockConnectionStateService = mock(ConnectionStateService.class);

        ActivityMessageCommandHandler handler
                = new ActivityMessageCommandHandler(mockAuthService, mockConnectionStateService);

        ActivityMessageCommand mockCommand = mock(ActivityMessageCommand.class);
        when(mockCommand.getUsername()).thenReturn("username");
        when(mockCommand.getSecret()).thenReturn("password");

        JsonObject activity = new JsonObject();
        when(mockCommand.getActivity()).thenReturn(activity);

        Connection mockConnection = mock(Connection.class);

        handler.handleCommandIncoming(mockCommand, mockConnection);

        verify(mockConnection).pushCommand(isA(AuthenticationFailCommand.class));
    }

    @Test
    public void testIfNotLoggedInThenSendAuthenticationFailCommand() {
        UserAuthService mockAuthService = mock(UserAuthService.class);
        when(mockAuthService.isLoggedIn(any(Connection.class))).thenReturn(false);
        when(mockAuthService
                .authorise(any(Connection.class), anyString(), anyString()))
                .thenReturn(false);

        ConnectionStateService mockConnectionStateService = mock(ConnectionStateService.class);

        ActivityMessageCommandHandler handler
                = new ActivityMessageCommandHandler(mockAuthService, mockConnectionStateService);

        ActivityMessageCommand mockCommand = mock(ActivityMessageCommand.class);
        when(mockCommand.getUsername()).thenReturn("username");
        when(mockCommand.getSecret()).thenReturn("password");

        JsonObject activity = new JsonObject();
        when(mockCommand.getActivity()).thenReturn(activity);

        Connection mockConnection = mock(Connection.class);

        handler.handleCommandIncoming(mockCommand, mockConnection);

        verify(mockConnection).pushCommand(isA(AuthenticationFailCommand.class));
    }

    @Test
    public void testIfAuthorisedThenBroadcastTheActivityObject() {
        UserAuthService mockAuthService = mock(UserAuthService.class);
        when(mockAuthService.isLoggedIn(any(Connection.class))).thenReturn(true);
        when(mockAuthService.authorise(any(Connection.class), anyString(), anyString())).thenReturn(true);

        ConnectionStateService mockConnectionStateService = mock(ConnectionStateService.class);

        ActivityMessageCommandHandler handler
                = new ActivityMessageCommandHandler(mockAuthService, mockConnectionStateService);

        ActivityMessageCommand mockCommand = mock(ActivityMessageCommand.class);
        when(mockCommand.getUsername()).thenReturn("username");
        when(mockCommand.getSecret()).thenReturn("password");

        JsonObject activity = new JsonObject();
        when(mockCommand.getActivity()).thenReturn(activity);

        Connection mockConnection = mock(Connection.class);

        handler.handleCommandIncoming(mockCommand, mockConnection);

        verify(mockConnectionStateService).broadcastToAll(isA(ActivityBroadcastCommand.class), same(mockConnection));
    }

    @Test
    public void testTheActivityObjectShouldHaveTheAuthenticatedUserFieldAdded() {
        UserAuthService mockAuthService = mock(UserAuthService.class);
        when(mockAuthService.isLoggedIn(any(Connection.class))).thenReturn(true);
        when(mockAuthService.authorise(any(Connection.class), anyString(), anyString())).thenReturn(true);

        ConnectionStateService mockConnectionStateService = mock(ConnectionStateService.class);

        ActivityMessageCommandHandler handler
                = new ActivityMessageCommandHandler(mockAuthService, mockConnectionStateService);



        ActivityMessageCommand mockCommand = mock(ActivityMessageCommand.class);
        when(mockCommand.getUsername()).thenReturn("username");
        when(mockCommand.getSecret()).thenReturn("password");

        JsonObject activity = new JsonObject();
        when(mockCommand.getActivity()).thenReturn(activity);

        Connection mockConnection = mock(Connection.class);

        handler.handleCommandIncoming(mockCommand, mockConnection);

        assertTrue(activity.has("authenticated_user"));
    }
}
