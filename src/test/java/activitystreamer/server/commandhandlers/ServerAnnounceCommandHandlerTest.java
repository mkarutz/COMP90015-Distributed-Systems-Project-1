package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.InvalidMessageCommand;
import activitystreamer.core.command.ServerAnnounceCommand;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.ConnectionStateService;
import activitystreamer.server.services.RemoteServerStateService;
import activitystreamer.server.services.ServerAuthService;
import org.junit.Test;
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

//    @Test
//    public void testIfTheHostnameIsNotPresentThenAnInvalidMessageCommandIsSent() {
//
//        ConnectionStateService mockConnectionStateService = mock(ConnectionStateService.class);
//        RemoteServerStateService mockRemoteServerStateService = mock(RemoteServerStateService.class);
//
//        ServerAnnounceCommandHandler handler = new ServerAnnounceCommandHandler(
//                mockRemoteServerStateService,
//                mockConnectionStateService
//        );
//
//        ServerAnnounceCommand mockCommand = mock(ServerAnnounceCommand.class);
//        when(mockCommand.getId()).thenReturn("not null ID");
//        when(mockCommand.getHostname()).thenReturn(null);
//
//        Connection mockConnection = mock(Connection.class);
//
//        handler.handleCommand(mockCommand, mockConnection);
//
//        verify(mockConnection).pushCommand(isA(InvalidMessageCommand.class));
//        verify(mockConnection).close();
//    }
}
