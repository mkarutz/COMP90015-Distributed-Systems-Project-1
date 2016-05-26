package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.ServerAuthService;
import activitystreamer.server.services.contracts.UserAuthService;
import activitystreamer.server.services.impl.ConcreteUserAuthService;
import activitystreamer.server.services.contracts.ConnectionManager;
import activitystreamer.server.services.contracts.ServerAuthService;
import activitystreamer.server.services.impl.NetworkManagerService;
import activitystreamer.server.services.contracts.BroadcastService;
import org.junit.Test;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class LockRequestCommandHandlerTest {
  @Test
  public void testIfReceivedFromUnauthorizedServerSendInvalidMessage() {
    Connection mockConnection = mock(Connection.class);

    ServerAuthService mockServerAuthService = mock(ServerAuthService.class);
    when(mockServerAuthService.isAuthenticated(mockConnection)).thenReturn(false);

    LockRequestCommandHandler handler = new LockRequestCommandHandler(
        mock(UserAuthService.class),
        mockServerAuthService,
        mock(ConnectionManager.class)
    );

    LockRequestCommand cmd = new LockRequestCommand("aaron", "isthebest", "123");

    handler.handleCommand(cmd, mockConnection);

    verify(mockConnection).pushCommand(isA(InvalidMessageCommand.class));
  }

  @Test
  public void testIfReceivedFromNonLegacyServerTheReplyWithInvalidMessage() {
    Connection mockConnection = mock(Connection.class);

    ServerAuthService mockServerAuthService = mock(ServerAuthService.class);
    when(mockServerAuthService.isAuthenticated(mockConnection)).thenReturn(true);

    ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
    when(mockConnectionManager.isLegacyServer(mockConnection)).thenReturn(false);

    LockRequestCommandHandler handler = new LockRequestCommandHandler(
        mock(UserAuthService.class),
        mockServerAuthService,
        mockConnectionManager
    );

    LockRequestCommand cmd = new LockRequestCommand("aaron", "isthebest", "123");

    handler.handleCommand(cmd, mockConnection);

    verify(mockConnection).pushCommand(isA(InvalidMessageCommand.class));
  }
}
