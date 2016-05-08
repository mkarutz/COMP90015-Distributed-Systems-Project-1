package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.ActivityBroadcastCommand;
import activitystreamer.core.command.ActivityMessageCommand;
import activitystreamer.core.command.AuthenticationFailCommand;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.ConnectionManager;
import activitystreamer.server.services.contracts.BroadcastService;
import activitystreamer.server.services.contracts.RemoteServerStateService;
import activitystreamer.server.services.contracts.UserAuthService;
import activitystreamer.server.services.impl.ConcreteRemoteServerStateService;
import activitystreamer.server.services.impl.ConcreteUserAuthService;
import com.google.gson.JsonObject;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ActivityMessageCommandHandlerTest {
    @Test
    public void testCanPostAsAnonymousWhenLoggedIn() {
        RemoteServerStateService mockServerService = mock(ConcreteRemoteServerStateService.class);
        BroadcastService mockBroadcastService = mock(BroadcastService.class);
        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);

        UserAuthService mockAuthService = spy(new ConcreteUserAuthService(mockServerService, mockConnectionManager,mockBroadcastService));
        when(mockAuthService.isLoggedIn(any(Connection.class))).thenReturn(true);



        ActivityMessageCommandHandler handler
                = new ActivityMessageCommandHandler(mockAuthService, mockBroadcastService, mockConnectionManager);

        ActivityMessageCommand mockCommand = mock(ActivityMessageCommand.class);
        when(mockCommand.getUsername()).thenReturn("anonymous");

        JsonObject activity = new JsonObject();
        when(mockCommand.getActivity()).thenReturn(activity);

        Connection mockConnection = mock(Connection.class);

        handler.handleCommand(mockCommand, mockConnection);

        verify(mockConnection, never()).pushCommand(isA(AuthenticationFailCommand.class));
    }

    @Test
    public void testIfNonAnonymousCredentialsDoNotMatchThenSendAuthenticationFailCommand() {
        UserAuthService mockAuthService = mock(ConcreteUserAuthService.class);
        when(mockAuthService.isLoggedIn(any(Connection.class))).thenReturn(true);
        when(mockAuthService
                .authorise(any(Connection.class), anyString(), anyString()))
                .thenReturn(false);

        BroadcastService mockBroadcastService = mock(BroadcastService.class);

        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);

        ActivityMessageCommandHandler handler
                = new ActivityMessageCommandHandler(mockAuthService, mockBroadcastService, mockConnectionManager);

        ActivityMessageCommand mockCommand = mock(ActivityMessageCommand.class);
        when(mockCommand.getUsername()).thenReturn("username");
        when(mockCommand.getSecret()).thenReturn("password");

        JsonObject activity = new JsonObject();
        when(mockCommand.getActivity()).thenReturn(activity);

        Connection mockConnection = mock(Connection.class);

        handler.handleCommand(mockCommand, mockConnection);

        verify(mockConnection).pushCommand(isA(AuthenticationFailCommand.class));
    }

    @Test
    public void testIfNotLoggedInThenSendAuthenticationFailCommand() {
        UserAuthService mockAuthService = mock(ConcreteUserAuthService.class);
        when(mockAuthService.isLoggedIn(any(Connection.class))).thenReturn(false);
        when(mockAuthService
                .authorise(any(Connection.class), anyString(), anyString()))
                .thenReturn(false);

        BroadcastService mockBroadcastService = mock(BroadcastService.class);

        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);

        ActivityMessageCommandHandler handler
                = new ActivityMessageCommandHandler(mockAuthService, mockBroadcastService, mockConnectionManager);

        ActivityMessageCommand mockCommand = mock(ActivityMessageCommand.class);
        when(mockCommand.getUsername()).thenReturn("username");
        when(mockCommand.getSecret()).thenReturn("password");

        JsonObject activity = new JsonObject();
        when(mockCommand.getActivity()).thenReturn(activity);

        Connection mockConnection = mock(Connection.class);

        handler.handleCommand(mockCommand, mockConnection);

        verify(mockConnection).pushCommand(isA(AuthenticationFailCommand.class));
    }

    @Test
    public void testIfAuthorisedThenBroadcastTheActivityObject() {
        UserAuthService mockAuthService = mock(ConcreteUserAuthService.class);
        when(mockAuthService.isLoggedIn(any(Connection.class))).thenReturn(true);
        when(mockAuthService.authorise(any(Connection.class), anyString(), anyString())).thenReturn(true);

        BroadcastService mockBroadcastService = mock(BroadcastService.class);

        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);

        ActivityMessageCommandHandler handler
                = new ActivityMessageCommandHandler(mockAuthService, mockBroadcastService, mockConnectionManager);

        ActivityMessageCommand mockCommand = mock(ActivityMessageCommand.class);
        when(mockCommand.getUsername()).thenReturn("username");
        when(mockCommand.getSecret()).thenReturn("password");

        JsonObject activity = new JsonObject();
        when(mockCommand.getActivity()).thenReturn(activity);

        Connection mockConnection = mock(Connection.class);

        handler.handleCommand(mockCommand, mockConnection);

        verify(mockBroadcastService).broadcastToAll(isA(ActivityBroadcastCommand.class));
    }

    @Test
    public void testTheActivityObjectShouldHaveTheAuthenticatedUserFieldAdded() {
        UserAuthService mockAuthService = mock(ConcreteUserAuthService.class);
        when(mockAuthService.isLoggedIn(any(Connection.class))).thenReturn(true);
        when(mockAuthService.authorise(any(Connection.class), anyString(), anyString())).thenReturn(true);

        BroadcastService mockBroadcastService = mock(BroadcastService.class);

        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);

        ActivityMessageCommandHandler handler
                = new ActivityMessageCommandHandler(mockAuthService, mockBroadcastService, mockConnectionManager);

        ActivityMessageCommand mockCommand = mock(ActivityMessageCommand.class);
        when(mockCommand.getUsername()).thenReturn("username");
        when(mockCommand.getSecret()).thenReturn("password");

        JsonObject activity = new JsonObject();
        when(mockCommand.getActivity()).thenReturn(activity);

        Connection mockConnection = mock(Connection.class);

        handler.handleCommand(mockCommand, mockConnection);

        assertTrue(activity.has("authenticated_user"));
    }
}
