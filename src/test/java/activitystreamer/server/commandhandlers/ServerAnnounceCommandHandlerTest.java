package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.InvalidMessageCommand;
import activitystreamer.core.command.ServerAnnounceCommand;
import activitystreamer.core.command.transmission.CommandParseException;
import activitystreamer.core.command.transmission.gson.GsonCommandSerializationAdaptor;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.ServerState;
import activitystreamer.server.services.ConnectionStateService;
import activitystreamer.server.services.RemoteServerStateService;
import activitystreamer.server.services.ServerAuthService;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class ServerAnnounceCommandHandlerTest {
    @Test
    public void testIfTheServerIdIsNotPresentThenAnInvalidMessageCommandIsSent() {

        ConnectionStateService mockConnectionStateService = mock(ConnectionStateService.class);
        RemoteServerStateService mockRemoteServerStateService = mock(RemoteServerStateService.class);

        ServerAnnounceCommandHandler handler = new ServerAnnounceCommandHandler(
                mockRemoteServerStateService,
                mockConnectionStateService
        );

        ServerAnnounceCommand mockCommand = mock(ServerAnnounceCommand.class);
        when(mockCommand.getId()).thenReturn(null);

        Connection mockConnection = mock(Connection.class);

        handler.handleCommand(mockCommand, mockConnection);

        verify(mockConnection).pushCommand(isA(InvalidMessageCommand.class));
        verify(mockConnection).close();
    }

    @Test
    public void testIfReceivedFromUnauthenticatedServerThenSendAnInvalidMessageCommand() throws CommandParseException {

        ConnectionStateService mockConnectionStateService = mock(ConnectionStateService.class);
        when(mockConnectionStateService.getConnectionType(any(Connection.class))).thenReturn(null);

        RemoteServerStateService mockRemoteServerStateService = mock(RemoteServerStateService.class);

        ServerAnnounceCommandHandler handler = new ServerAnnounceCommandHandler(
                mockRemoteServerStateService,
                mockConnectionStateService
        );

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

        ConnectionStateService mockConnectionStateService = mock(ConnectionStateService.class);
        when(mockConnectionStateService.getConnectionType(any(Connection.class))).thenReturn(ConnectionStateService.ConnectionType.SERVER);

        RemoteServerStateService mockRemoteServerStateService = mock(RemoteServerStateService.class);

        ServerAnnounceCommandHandler handler = new ServerAnnounceCommandHandler(
                mockRemoteServerStateService,
                mockConnectionStateService
        );

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
    public void testTheMessageIsBroadcasted() throws CommandParseException, UnknownHostException {

        ConnectionStateService mockConnectionStateService = mock(ConnectionStateService.class);
        when(mockConnectionStateService.getConnectionType(any(Connection.class))).thenReturn(ConnectionStateService.ConnectionType.SERVER);

        RemoteServerStateService mockRemoteServerStateService = mock(RemoteServerStateService.class);

        ServerAnnounceCommandHandler handler = new ServerAnnounceCommandHandler(
                mockRemoteServerStateService,
                mockConnectionStateService
        );

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

        verify(mockConnectionStateService).broadcastToServers(mockCommand, mockConnection);
    }
}
