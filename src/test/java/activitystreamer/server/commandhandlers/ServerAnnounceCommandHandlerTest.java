package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.InvalidMessageCommand;
import activitystreamer.core.command.ServerAnnounceCommand;
import activitystreamer.core.command.transmission.CommandParseException;
import activitystreamer.core.command.transmission.gson.GsonCommandSerializationAdaptor;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.ServerState;
import activitystreamer.server.services.contracts.BroadcastService;
import activitystreamer.server.services.contracts.RemoteServerStateService;
import activitystreamer.server.services.contracts.ServerAuthService;
import activitystreamer.server.services.impl.NetworkManagerService;
import activitystreamer.server.services.contracts.UserAuthService;
import activitystreamer.server.services.impl.ConcreteUserAuthService;
import activitystreamer.server.services.impl.ConcreteRemoteServerStateService;
import activitystreamer.server.services.contracts.ConnectionManager;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.mockito.Mockito.*;

public class ServerAnnounceCommandHandlerTest {
    @Test
    public void testIfTheServerIdIsNotPresentThenAnInvalidMessageCommandIsSent() {
        ServerAuthService mockServerAuthService = mock(NetworkManagerService.class);
        when(mockServerAuthService.isAuthenticated(any(Connection.class))).thenReturn(true);
        BroadcastService mockBroadcastService = mock(BroadcastService.class);
        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);

        UserAuthService mockAuthService = mock(ConcreteUserAuthService.class);

        RemoteServerStateService mockRemoteServerStateService = mock(ConcreteRemoteServerStateService.class);

        ServerAnnounceCommandHandler handler = new ServerAnnounceCommandHandler(
                mockRemoteServerStateService,
                mockBroadcastService,
                mockServerAuthService,
                mockConnectionManager);

        ServerAnnounceCommand mockCommand = mock(ServerAnnounceCommand.class);
        when(mockCommand.getId()).thenReturn(null);

        Connection mockConnection = mock(Connection.class);

        handler.handleCommand(mockCommand, mockConnection);

        verify(mockConnection).pushCommand(isA(InvalidMessageCommand.class));
        verify(mockConnectionManager).closeConnection(mockConnection);
    }

    @Test
    public void testIfReceivedFromUnauthenticatedServerThenSendAnInvalidMessageCommand() throws CommandParseException {
        ServerAuthService mockServerAuthService = mock(NetworkManagerService.class);
        when(mockServerAuthService.isAuthenticated(any(Connection.class))).thenReturn(false);

        BroadcastService mockBroadcastService = mock(BroadcastService.class);
        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);

        UserAuthService mockAuthService = mock(ConcreteUserAuthService.class);

        RemoteServerStateService mockRemoteServerStateService = mock(ConcreteRemoteServerStateService.class);

        ServerAnnounceCommandHandler handler = new ServerAnnounceCommandHandler(
                mockRemoteServerStateService,
                mockBroadcastService,
                mockServerAuthService,
                mockConnectionManager);

        ServerAnnounceCommand mockCommand = (ServerAnnounceCommand) new GsonCommandSerializationAdaptor().deserialize(
                "{\n" +
                "    \"command\" : \"SERVER_ANNOUNCE\",\n" +
                "    \"id\" : \"fmnmpp3ai91qb3gc2bvs14g3ue\",\n" +
                "    \"load\" : 5,\n" +
                "    \"hostname\" : \"128.250.13.46\",\n" +
                "    \"port\" : 3570\n" +
                "}"
        );

        Connection mockConnection = mock(Connection.class);

        handler.handleCommand(mockCommand, mockConnection);

        verify(mockConnection).pushCommand(isA(InvalidMessageCommand.class));
        verify(mockConnectionManager).closeConnection(mockConnection);
    }

    @Test
    public void testServerStateIsUpdated() throws CommandParseException, UnknownHostException {
        ServerAuthService mockServerAuthService = mock(NetworkManagerService.class);
        when(mockServerAuthService.isAuthenticated(any(Connection.class))).thenReturn(true);

        BroadcastService mockBroadcastService = mock(BroadcastService.class);
        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);

        UserAuthService mockAuthService = mock(ConcreteUserAuthService.class);

        RemoteServerStateService mockRemoteServerStateService = mock(ConcreteRemoteServerStateService.class);

        ServerAnnounceCommandHandler handler = new ServerAnnounceCommandHandler(
                mockRemoteServerStateService,
                mockBroadcastService,
                mockServerAuthService,
                mockConnectionManager);

        ServerAnnounceCommand mockCommand = (ServerAnnounceCommand) new GsonCommandSerializationAdaptor().deserialize(
                "{\n" +
                "    \"command\" : \"SERVER_ANNOUNCE\",\n" +
                "    \"id\" : \"fmnmpp3ai91qb3gc2bvs14g3ue\",\n" +
                "    \"load\" : 5,\n" +
                "    \"hostname\" : \"128.250.13.46\",\n" +
                "    \"port\" : 3570,\n" +
                "    \"securePort\" : 3571\n" +
                "}"
        );

        Connection mockConnection = mock(Connection.class);

        handler.handleCommand(mockCommand, mockConnection);

        InetAddress address = InetAddress.getByName("128.250.13.46");
        ServerState state = new ServerState(address, 3570, 5, -1);

        verify(mockRemoteServerStateService).updateState(eq("fmnmpp3ai91qb3gc2bvs14g3ue"), eq(5), eq(address), eq(3570), eq(3571));

        verify(mockConnection, never()).pushCommand(isA(InvalidMessageCommand.class));
        verify(mockConnectionManager,never()).closeConnection(mockConnection);
    }

    @Test
    public void testHandleMissingFieldsForBackwardsCompatibility() throws CommandParseException, UnknownHostException {
        ServerAuthService mockServerAuthService = mock(NetworkManagerService.class);
        when(mockServerAuthService.isAuthenticated(any(Connection.class))).thenReturn(true);

        BroadcastService mockBroadcastService = mock(BroadcastService.class);
        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);

        UserAuthService mockAuthService = mock(ConcreteUserAuthService.class);

        RemoteServerStateService mockRemoteServerStateService = mock(ConcreteRemoteServerStateService.class);

        ServerAnnounceCommandHandler handler = new ServerAnnounceCommandHandler(
                mockRemoteServerStateService,
                mockBroadcastService,
                mockServerAuthService,
                mockConnectionManager);

        ServerAnnounceCommand mockCommand = (ServerAnnounceCommand) new GsonCommandSerializationAdaptor().deserialize(
                "{\n" +
                        "    \"command\" : \"SERVER_ANNOUNCE\",\n" +
                        "    \"id\" : \"fmnmpp3ai91qb3gc2bvs14g3ue\",\n" +
                        "    \"load\" : 5,\n" +
                        "    \"hostname\" : \"128.250.13.46\",\n" +
                        "    \"port\" : 3570\n" +
                        "}"
        );

        Connection mockConnection = mock(Connection.class);

        handler.handleCommand(mockCommand, mockConnection);

        InetAddress address = InetAddress.getByName("128.250.13.46");
        ServerState state = new ServerState(address, 3570, 5, -1);

        verify(mockRemoteServerStateService).updateState(eq("fmnmpp3ai91qb3gc2bvs14g3ue"), eq(5), eq(address), eq(3570), eq(-1));

        verify(mockConnection, never()).pushCommand(isA(InvalidMessageCommand.class));
        verify(mockConnectionManager,never()).closeConnection(mockConnection);
    }

    @Test
    public void testTheMessageIsBroadcast() throws CommandParseException, UnknownHostException {
        ServerAuthService mockServerAuthService = mock(NetworkManagerService.class);
        when(mockServerAuthService.isAuthenticated(any(Connection.class))).thenReturn(true);

        BroadcastService mockBroadcastService = mock(BroadcastService.class);
        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);

        UserAuthService mockAuthService = mock(ConcreteUserAuthService.class);

        RemoteServerStateService mockRemoteServerStateService = mock(ConcreteRemoteServerStateService.class);

        ServerAnnounceCommandHandler handler = new ServerAnnounceCommandHandler(
                mockRemoteServerStateService,
                mockBroadcastService,
                mockServerAuthService,
                mockConnectionManager);

        ServerAnnounceCommand mockCommand = (ServerAnnounceCommand) new GsonCommandSerializationAdaptor().deserialize(
                "{\n" +
                "    \"command\" : \"SERVER_ANNOUNCE\",\n" +
                "    \"id\" : \"fmnmpp3ai91qb3gc2bvs14g3ue\",\n" +
                "    \"load\" : 5,\n" +
                "    \"hostname\" : \"128.250.13.46\",\n" +
                "    \"port\" : 3570\n" +
                "}"
        );

        Connection mockConnection = mock(Connection.class);

        handler.handleCommand(mockCommand, mockConnection);

        verify(mockBroadcastService).broadcastToServers(mockCommand, mockConnection);
    }
}
