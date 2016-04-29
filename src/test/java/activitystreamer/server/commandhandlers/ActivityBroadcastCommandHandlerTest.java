package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.ActivityBroadcastCommand;
import activitystreamer.core.command.ICommand;
import activitystreamer.core.command.InvalidMessageCommand;
import activitystreamer.core.command.transmission.CommandParseException;
import activitystreamer.core.command.transmission.gson.GsonCommandSerializationAdaptor;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.BroadcastService;
import activitystreamer.server.services.contracts.ServerAuthService;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

public class ActivityBroadcastCommandHandlerTest {
    @Test
    public void testIfReceivedFromUnauthenticatedServerThenSendAnInvalidMessageCommand() throws CommandParseException {
        ServerAuthService mockServerAuthService = mock(ServerAuthService.class);
        when(mockServerAuthService.isAuthenticated(any(Connection.class))).thenReturn(false);

        BroadcastService mockBroadcastService = mock(BroadcastService.class);

        ActivityBroadcastCommandHandler handler = new ActivityBroadcastCommandHandler(
                mockServerAuthService,
                mockBroadcastService
        );

        ActivityBroadcastCommand mockCommand = (ActivityBroadcastCommand)
                new GsonCommandSerializationAdaptor().deserialize(
                        "{" +
                        "    \"command\" : \"ACTIVITY_BROADCAST\"," +
                        "    \"activity\" :  {" +
                        "        \"authenticated_user\" : \"aaron\"" +
                        "    }" +
                        "}"
                );

        Connection mockConnection = mock(Connection.class);

        handler.handleCommand(mockCommand, mockConnection);

        verify(mockBroadcastService, never()).broadcastToAll(any(ICommand.class), any(Connection.class));
        verify(mockConnection).pushCommand(isA(InvalidMessageCommand.class));
        verify(mockConnection).close();
    }

    @Test
    public void testIfAuthorisedThenBroadcastToAllClientsAndServers() throws CommandParseException {
        ServerAuthService mockServerAuthService = mock(ServerAuthService.class);
        when(mockServerAuthService.isAuthenticated(any(Connection.class))).thenReturn(true);
        BroadcastService mockBroadcastService = mock(BroadcastService.class);

        ActivityBroadcastCommandHandler handler = new ActivityBroadcastCommandHandler(
                mockServerAuthService,
                mockBroadcastService
        );

        ActivityBroadcastCommand command = (ActivityBroadcastCommand)
                new GsonCommandSerializationAdaptor().deserialize(
                        "{" +
                        "    \"command\" : \"ACTIVITY_BROADCAST\"," +
                        "    \"activity\" :  {" +
                        "        \"authenticated_user\" : \"aaron\"" +
                        "    }" +
                        "}"
                );

        System.out.println(command.getActivity());

        Assert.assertTrue(command.getActivity().has("authenticated_user"));

        Connection mockConnection = mock(Connection.class);

        handler.handleCommand(command, mockConnection);

        verify(mockBroadcastService).broadcastToAll(command, mockConnection);
        verify(mockConnection, never()).pushCommand(isA(InvalidMessageCommand.class));
        verify(mockConnection, never()).close();
    }

    @Test
    public void testIfTheActivityObjectHasNotBeenProcessedSendAnInvalidMessageCommand() throws CommandParseException {
        ServerAuthService mockServerAuthService = mock(ServerAuthService.class);
        when(mockServerAuthService.isAuthenticated(any(Connection.class))).thenReturn(true);
        BroadcastService mockBroadcastService = mock(BroadcastService.class);

        ActivityBroadcastCommandHandler handler = new ActivityBroadcastCommandHandler(
                mockServerAuthService,
                mockBroadcastService
        );

        ActivityBroadcastCommand mockCommand = (ActivityBroadcastCommand)
                new GsonCommandSerializationAdaptor().deserialize(
                        "{" +
                        "    \"command\" : \"ACTIVITY_BROADCAST\"," +
                        "    \"activity\" :  {}" +
                        "}"
                );

        Connection mockConnection = mock(Connection.class);

        handler.handleCommand(mockCommand, mockConnection);

        verify(mockBroadcastService, never()).broadcastToAll(any(ICommand.class), any(Connection.class));
        verify(mockConnection).pushCommand(isA(InvalidMessageCommand.class));
        verify(mockConnection).close();
    }
}
