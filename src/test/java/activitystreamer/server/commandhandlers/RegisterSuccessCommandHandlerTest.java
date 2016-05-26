package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.InvalidMessageCommand;
import activitystreamer.core.command.RegisterSuccessCommand;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.ConnectionManager;
import activitystreamer.server.services.contracts.ServerAuthService;
import activitystreamer.server.services.contracts.UserAuthService;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class RegisterSuccessCommandHandlerTest {
  @Test
  public void testIfServerNotAuthenticatedSendInvalidMessage() {
    ServerAuthService mockServerAuthService = mock(ServerAuthService.class);
    when(mockServerAuthService.isAuthenticated(any(Connection.class))).thenReturn(false);

    RegisterSuccessCommandHandler handler = new RegisterSuccessCommandHandler(
        mock(UserAuthService.class),
        mockServerAuthService,
        mock(ConnectionManager.class)
    );

    RegisterSuccessCommand cmd = new RegisterSuccessCommand("info");
    Connection mockConnection = mock(Connection.class);

    handler.handleCommand(cmd, mockConnection);

    verify(mockConnection).pushCommand(isA(InvalidMessageCommand.class));
  }

  @Test
  public void testIfCommandComesFromNonParentConnectionSendInvalidMessage() {
    ServerAuthService mockServerAuthService = mock(ServerAuthService.class);
    when(mockServerAuthService.isAuthenticated(any(Connection.class))).thenReturn(true);

    ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
    when(mockConnectionManager.isParentConnection(any(Connection.class))).thenReturn(false);

    RegisterSuccessCommandHandler handler = new RegisterSuccessCommandHandler(
        mock(UserAuthService.class),
        mockServerAuthService,
        mockConnectionManager
    );

    RegisterSuccessCommand cmd = new RegisterSuccessCommand("info");
    Connection mockConnection = mock(Connection.class);

    handler.handleCommand(cmd, mockConnection);

    verify(mockConnection).pushCommand(isA(InvalidMessageCommand.class));
  }
}
