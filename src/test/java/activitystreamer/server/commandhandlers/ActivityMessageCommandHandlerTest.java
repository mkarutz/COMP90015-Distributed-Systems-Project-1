package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.ActivityBroadcastCommand;
import activitystreamer.core.command.ActivityMessageCommand;
import activitystreamer.core.command.AuthenticationFailCommand;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.IBroadcastService;
import activitystreamer.server.services.contracts.IUserAuthService;
import activitystreamer.server.services.impl.ConnectionStateService;
import activitystreamer.server.services.impl.RemoteServerStateService;
import activitystreamer.server.services.impl.UserAuthService;
import com.google.gson.JsonObject;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ActivityMessageCommandHandlerTest {
    @Test
    public void testCanPostAsAnonymousWhenLoggedIn() {
        RemoteServerStateService mockServerService = mock(RemoteServerStateService.class);
        IBroadcastService mockIBroadcastService = mock(ConnectionStateService.class);

        IUserAuthService mockAuthService = spy(new UserAuthService(mockServerService, mockIBroadcastService));
        when(mockAuthService.isLoggedIn(any(Connection.class))).thenReturn(true);

        ActivityMessageCommandHandler handler
                = new ActivityMessageCommandHandler(mockAuthService, mockIBroadcastService);

        ActivityMessageCommand mockCommand = mock(ActivityMessageCommand.class);
        when(mockCommand.getUsername()).thenReturn("anonymous");

        JsonObject activity = new JsonObject();
        when(mockCommand.getActivity()).thenReturn(activity);

        Connection mockConnection = mock(Connection.class);

        handler.handleCommand(mockCommand, mockConnection);

        verify(mockConnection, never()).pushCommand(isA(AuthenticationFailCommand.class));
    }

    @Test
    public void testIfNonAnonymousCredentialsDoNotMatchThenSendAuthenticaionFailCommand() {
        IUserAuthService mockAuthService = mock(UserAuthService.class);
        when(mockAuthService.isLoggedIn(any(Connection.class))).thenReturn(true);
        when(mockAuthService
                .authorise(any(Connection.class), anyString(), anyString()))
                .thenReturn(false);

        IBroadcastService mockIBroadcastService = mock(ConnectionStateService.class);

        ActivityMessageCommandHandler handler
                = new ActivityMessageCommandHandler(mockAuthService, mockIBroadcastService);

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
        IUserAuthService mockAuthService = mock(UserAuthService.class);
        when(mockAuthService.isLoggedIn(any(Connection.class))).thenReturn(false);
        when(mockAuthService
                .authorise(any(Connection.class), anyString(), anyString()))
                .thenReturn(false);

        IBroadcastService mockIBroadcastService = mock(ConnectionStateService.class);

        ActivityMessageCommandHandler handler
                = new ActivityMessageCommandHandler(mockAuthService, mockIBroadcastService);

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
        IUserAuthService mockAuthService = mock(UserAuthService.class);
        when(mockAuthService.isLoggedIn(any(Connection.class))).thenReturn(true);
        when(mockAuthService.authorise(any(Connection.class), anyString(), anyString())).thenReturn(true);

        IBroadcastService mockIBroadcastService = mock(ConnectionStateService.class);

        ActivityMessageCommandHandler handler
                = new ActivityMessageCommandHandler(mockAuthService, mockIBroadcastService);

        ActivityMessageCommand mockCommand = mock(ActivityMessageCommand.class);
        when(mockCommand.getUsername()).thenReturn("username");
        when(mockCommand.getSecret()).thenReturn("password");

        JsonObject activity = new JsonObject();
        when(mockCommand.getActivity()).thenReturn(activity);

        Connection mockConnection = mock(Connection.class);

        handler.handleCommand(mockCommand, mockConnection);

        verify(mockIBroadcastService).broadcastToAll(isA(ActivityBroadcastCommand.class), same(mockConnection));
    }

    @Test
    public void testTheActivityObjectShouldHaveTheAuthenticatedUserFieldAdded() {
        IUserAuthService mockAuthService = mock(UserAuthService.class);
        when(mockAuthService.isLoggedIn(any(Connection.class))).thenReturn(true);
        when(mockAuthService.authorise(any(Connection.class), anyString(), anyString())).thenReturn(true);

        IBroadcastService mockIBroadcastService = mock(ConnectionStateService.class);

        ActivityMessageCommandHandler handler
                = new ActivityMessageCommandHandler(mockAuthService, mockIBroadcastService);



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
