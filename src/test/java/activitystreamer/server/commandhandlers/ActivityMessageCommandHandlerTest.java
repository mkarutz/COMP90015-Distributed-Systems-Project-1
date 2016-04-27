package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.RemoteServerStateService;
import activitystreamer.server.services.UserAuthService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.Test;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class ActivityMessageCommandHandlerTest {
    @Test
    public void testCanPostAsAnonymousWhenLoggedIn() {
        RemoteServerStateService mockServerService = mock(RemoteServerStateService.class);

        UserAuthService mockAuthService = spy(new UserAuthService(mockServerService));
        when(mockAuthService.isLoggedIn(any(Connection.class))).thenReturn(true);

        ICommandBroadcaster mockBroadcastService = mock(ICommandBroadcaster.class);

        ActivityMessageCommandHandler handler
                = new ActivityMessageCommandHandler(mockAuthService, mockBroadcastService);

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

        ICommandBroadcaster mockBroadcastService = mock(ICommandBroadcaster.class);

        ActivityMessageCommandHandler handler
                = new ActivityMessageCommandHandler(mockAuthService, mockBroadcastService);

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

        ICommandBroadcaster mockBroadcastService = mock(ICommandBroadcaster.class);

        ActivityMessageCommandHandler handler
                = new ActivityMessageCommandHandler(mockAuthService, mockBroadcastService);

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

        ICommandBroadcaster mockBroadcastService = mock(ICommandBroadcaster.class);

        ActivityMessageCommandHandler handler
                = new ActivityMessageCommandHandler(mockAuthService, mockBroadcastService);

        ActivityMessageCommand mockCommand = mock(ActivityMessageCommand.class);
        when(mockCommand.getUsername()).thenReturn("username");
        when(mockCommand.getSecret()).thenReturn("password");

        JsonObject activity = new JsonObject();
        when(mockCommand.getActivity()).thenReturn(activity);

        Connection mockConnection = mock(Connection.class);

        handler.handleCommandIncoming(mockCommand, mockConnection);

        verify(mockBroadcastService).broadcast(isA(ActivityBroadcastCommand.class), same(mockConnection));
    }

    @Test
    public void testTheActivityObjectShouldHaveTheAuthenticatedUserFieldAdded() {
        UserAuthService mockAuthService = mock(UserAuthService.class);
        when(mockAuthService.isLoggedIn(any(Connection.class))).thenReturn(true);
        when(mockAuthService.authorise(any(Connection.class), anyString(), anyString())).thenReturn(true);

        ICommandBroadcaster mockBroadcastService = mock(ICommandBroadcaster.class);

        ActivityMessageCommandHandler handler
                = new ActivityMessageCommandHandler(mockAuthService, mockBroadcastService);



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
