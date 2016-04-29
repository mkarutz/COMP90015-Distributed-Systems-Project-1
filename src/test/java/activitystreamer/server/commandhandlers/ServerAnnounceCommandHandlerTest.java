package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.InvalidMessageCommand;
import activitystreamer.core.command.ServerAnnounceCommand;
import activitystreamer.core.command.transmission.CommandParseException;
import activitystreamer.core.command.transmission.gson.GsonCommandSerializationAdaptor;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.ServerState;
import activitystreamer.server.services.contracts.IBroadcastService;
import activitystreamer.server.services.contracts.IServerAuthService;
import activitystreamer.server.services.impl.ConnectionStateService;
import activitystreamer.server.services.impl.RemoteServerStateService;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.mockito.Mockito.*;

public class ServerAnnounceCommandHandlerTest {
    @Test
    public void testIfTheServerIdIsNotPresentThenAnInvalidMessageCommandIsSent() {
        IServerAuthService mockServerAuthService = mock(IServerAuthService.class);
        IBroadcastService mockIBroadcastService = mock(ConnectionStateService.class);
        RemoteServerStateService mockRemoteServerStateService = mock(RemoteServerStateService.class);

        ServerAnnounceCommandHandler handler = new ServerAnnounceCommandHandler(
                mockServerAuthService,
                mockRemoteServerStateService,
                mockIBroadcastService);

        ServerAnnounceCommand mockCommand = mock(ServerAnnounceCommand.class);
        when(mockCommand.getId()).thenReturn(null);

        Connection mockConnection = mock(Connection.class);

        handler.handleCommand(mockCommand, mockConnection);

        verify(mockConnection).pushCommand(isA(InvalidMessageCommand.class));
        verify(mockConnection).close();
    }

    @Test
    public void testIfReceivedFromUnauthenticatedServerThenSendAnInvalidMessageCommand() throws CommandParseException {
        IServerAuthService mockServerAuthService = mock(IServerAuthService.class);
        when(mockServerAuthService.isAuthenticated(any(Connection.class))).thenReturn(false);

        ConnectionStateService mockConnectionStateService = mock(ConnectionStateService.class);
        RemoteServerStateService mockRemoteServerStateService = mock(RemoteServerStateService.class);

        ServerAnnounceCommandHandler handler = new ServerAnnounceCommandHandler(
                mockServerAuthService,
                mockRemoteServerStateService,
                mockConnectionStateService);

        ServerAnnounceCommand mockCommand = (ServerAnnounceCommand) new GsonCommandSerializationAdaptor().deserialize("{\n" +
                "    \"command\" : \"SERVER_ANNOUNCE\",\n" +
                "    \"id\" : \"fmnmpp3ai91qb3gc2bvs14g3ue\",\n" +
                "    \"load\" : 5,\n" +
                "    \"hostname\" : \"128.250.13.46\",\n" +
                "    \"port\" : 3570\n" +
                "}");

        Connection mockConnection = mock(Connection.class);

        handler.handleCommand(mockCommand, mockConnection);

        verify(mockConnection).pushCommand(isA(InvalidMessageCommand.class));
        verify(mockConnection).close();
    }

    @Test
    public void testServerStateIsUpdated() throws CommandParseException, UnknownHostException {
        IServerAuthService mockServerAuthService = mock(IServerAuthService.class);
        when(mockServerAuthService.isAuthenticated(any(Connection.class))).thenReturn(true);

        ConnectionStateService mockConnectionStateService = mock(ConnectionStateService.class);

        RemoteServerStateService mockRemoteServerStateService = mock(RemoteServerStateService.class);

        ServerAnnounceCommandHandler handler = new ServerAnnounceCommandHandler(
                mockServerAuthService,
                mockRemoteServerStateService,
                mockConnectionStateService);

        ServerAnnounceCommand mockCommand = (ServerAnnounceCommand) new GsonCommandSerializationAdaptor().deserialize("{\n" +
                "    \"command\" : \"SERVER_ANNOUNCE\",\n" +
                "    \"id\" : \"fmnmpp3ai91qb3gc2bvs14g3ue\",\n" +
                "    \"load\" : 5,\n" +
                "    \"hostname\" : \"128.250.13.46\",\n" +
                "    \"port\" : 3570\n" +
                "}");

        Connection mockConnection = mock(Connection.class);

        handler.handleCommand(mockCommand, mockConnection);

        InetAddress address = InetAddress.getByName("128.250.13.46");
        ServerState state = new ServerState(address, 3570, 5);

        verify(mockRemoteServerStateService).updateState(eq("fmnmpp3ai91qb3gc2bvs14g3ue"), eq(state));

        verify(mockConnection, never()).pushCommand(isA(InvalidMessageCommand.class));
        verify(mockConnection, never()).close();
    }

    @Test
    public void testTheMessageIsBroadcast() throws CommandParseException, UnknownHostException {
        IServerAuthService mockServerAuthService = mock(IServerAuthService.class);
        when(mockServerAuthService.isAuthenticated(any(Connection.class))).thenReturn(true);

        ConnectionStateService mockConnectionStateService = mock(ConnectionStateService.class);
        RemoteServerStateService mockRemoteServerStateService = mock(RemoteServerStateService.class);

        ServerAnnounceCommandHandler handler = new ServerAnnounceCommandHandler(
                mockServerAuthService,
                mockRemoteServerStateService,
                mockConnectionStateService);

        ServerAnnounceCommand mockCommand = (ServerAnnounceCommand) new GsonCommandSerializationAdaptor().deserialize("{\n" +
                "    \"command\" : \"SERVER_ANNOUNCE\",\n" +
                "    \"id\" : \"fmnmpp3ai91qb3gc2bvs14g3ue\",\n" +
                "    \"load\" : 5,\n" +
                "    \"hostname\" : \"128.250.13.46\",\n" +
                "    \"port\" : 3570\n" +
                "}");

        Connection mockConnection = mock(Connection.class);

        handler.handleCommand(mockCommand, mockConnection);

        verify(mockConnectionStateService).broadcastToServers(mockCommand, mockConnection);
    }
}
