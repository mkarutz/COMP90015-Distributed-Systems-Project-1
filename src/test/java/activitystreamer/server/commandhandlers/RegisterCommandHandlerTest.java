package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.*;
import activitystreamer.server.services.impl.ConcreteUserAuthService;
import activitystreamer.server.services.impl.NetworkManagerService;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class RegisterCommandHandlerTest {
  @Test
  public void testUsernameMustBeNotNull() {
    UserAuthService mockAuthService = mock(ConcreteUserAuthService.class);
    ServerAuthService mockServerAuthService = mock(NetworkManagerService.class);

    ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
    RegisterCommandHandler handler = new RegisterCommandHandler(mockAuthService, mockServerAuthService, mockConnectionManager);

    RegisterCommand mockCommand = mock(RegisterCommand.class);
    when(mockCommand.getUsername()).thenReturn(null);

    Connection mockConnection = mock(Connection.class);

    handler.handleCommand(mockCommand, mockConnection);

    verify(mockConnection).pushCommand(isA(InvalidMessageCommand.class));
  }

  @Test
  public void testSecretMustBeNotNull() {
    UserAuthService mockAuthService = mock(ConcreteUserAuthService.class);
    ServerAuthService mockServerAuthService = mock(NetworkManagerService.class);

    ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
    RegisterCommandHandler handler = new RegisterCommandHandler(mockAuthService, mockServerAuthService, mockConnectionManager);

    RegisterCommand mockCommand = mock(RegisterCommand.class);
    when(mockCommand.getUsername()).thenReturn("username");
    when(mockCommand.getSecret()).thenReturn(null);

    Connection mockConnection = mock(Connection.class);

    handler.handleCommand(mockCommand, mockConnection);

    verify(mockConnection).pushCommand(isA(InvalidMessageCommand.class));
  }

  @Test
  public void testRegisterIsCalledOnTheAuthService() {
    UserAuthService mockAuthService = mock(ConcreteUserAuthService.class);
    when(mockAuthService.register(anyString(), anyString(), any(Connection.class))).thenReturn(false);

    ServerAuthService mockServerAuthService = mock(NetworkManagerService.class);

    ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
    RegisterCommandHandler handler = new RegisterCommandHandler(mockAuthService, mockServerAuthService, mockConnectionManager);

    RegisterCommand mockCommand = mock(RegisterCommand.class);
    when(mockCommand.getUsername()).thenReturn("username");
    when(mockCommand.getSecret()).thenReturn("password");

    Connection mockConnection = mock(Connection.class);

    handler.handleCommand(mockCommand, mockConnection);

    verify(mockAuthService).register(mockCommand.getUsername(), mockCommand.getSecret(), mockConnection);
  }

  @Test
  public void testIfTheUsernameIsAlreadyKnownThenSendARegisterFailedCommand() {
    UserAuthService mockAuthService = mock(ConcreteUserAuthService.class);
    when(mockAuthService.register(anyString(), anyString(), any(Connection.class))).thenReturn(false);

    ServerAuthService mockServerAuthService = mock(NetworkManagerService.class);

    ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
    RegisterCommandHandler handler = new RegisterCommandHandler(mockAuthService, mockServerAuthService, mockConnectionManager);

    RegisterCommand mockCommand = mock(RegisterCommand.class);
    when(mockCommand.getUsername()).thenReturn("username");
    when(mockCommand.getSecret()).thenReturn("password");

    Connection mockConnection = mock(Connection.class);

    handler.handleCommand(mockCommand, mockConnection);

    verify(mockConnection).pushCommand(isA(RegisterFailedCommand.class));
  }

  @Test
  public void testWhenUsernameNotPresentSendRegisterCommandToParent() {
    Connection mockParentConnection = mock(Connection.class);
    ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
    when(mockConnectionManager.getParentConnection()).thenReturn(mockParentConnection);
    when(mockConnectionManager.hasParent()).thenReturn(true);

    RemoteServerStateService mockRSSS = mock(RemoteServerStateService.class);
    BroadcastService mockBS = mock(BroadcastService.class);

    UserAuthService authService = new ConcreteUserAuthService(mockRSSS, mockConnectionManager, mockBS);

    ServerAuthService mockServerAuthService = mock(NetworkManagerService.class);

    RegisterCommandHandler handler = new RegisterCommandHandler(
        authService,
        mockServerAuthService,
        mockConnectionManager
    );

    RegisterCommand mockCommand = mock(RegisterCommand.class);
    when(mockCommand.getUsername()).thenReturn("username");
    when(mockCommand.getSecret()).thenReturn("password");

    Connection mockConnection = mock(Connection.class);

    handler.handleCommand(mockCommand, mockConnection);

    verify(mockParentConnection).pushCommand(isA(RegisterCommand.class));
  }

  @Test
  public void testWhenUsernameNotPresentAtRootSendRegisterSuccess() {
    ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
    when(mockConnectionManager.hasParent()).thenReturn(false);

    RemoteServerStateService mockRSSS = mock(RemoteServerStateService.class);
    BroadcastService mockBS = mock(BroadcastService.class);

    UserAuthService authService = new ConcreteUserAuthService(mockRSSS, mockConnectionManager, mockBS);

    ServerAuthService mockServerAuthService = mock(NetworkManagerService.class);

    RegisterCommandHandler handler = new RegisterCommandHandler(
        authService,
        mockServerAuthService,
        mockConnectionManager
    );

    RegisterCommand mockCommand = mock(RegisterCommand.class);
    when(mockCommand.getUsername()).thenReturn("username");
    when(mockCommand.getSecret()).thenReturn("password");

    Connection mockConnection = mock(Connection.class);

    handler.handleCommand(mockCommand, mockConnection);

    verify(mockConnection).pushCommand(isA(RegisterSuccessCommand.class));
  }
}
