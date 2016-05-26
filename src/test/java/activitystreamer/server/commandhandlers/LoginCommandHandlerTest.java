package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.BroadcastService;
import activitystreamer.server.services.contracts.RemoteServerStateService;
import activitystreamer.server.services.contracts.UserAuthService;
import activitystreamer.server.services.impl.ConcreteRemoteServerStateService;
import activitystreamer.server.services.impl.ConcreteUserAuthService;
import activitystreamer.server.services.contracts.ConnectionManager;
import activitystreamer.server.services.contracts.ServerAuthService;
import activitystreamer.server.services.impl.NetworkManagerService;
import org.junit.Test;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class LoginCommandHandlerTest {
  //    @Test
//    public void aUsernameMustBePreset() {
//        UserAuthService mockAuthService = mock(ConcreteUserAuthService.class);
//        ServerAuthService mockServerAuthService = mock(NetworkManagerService.class);
//        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
//        RemoteServerStateService mockRemoteServerStateService = mock(ConcreteRemoteServerStateService.class);
//
//        BroadcastService broadcastService = mock(BroadcastService.class);
//
//        LoginCommandHandler handler = new LoginCommandHandler(mockAuthService,mockServerAuthService,mockRemoteServerStateService,mockConnectionManager);
//
//        LoginCommand cmd = mock(LoginCommand.class);
//        when(cmd.getUsername()).thenReturn(null);
//
//        Connection conn = mock(Connection.class);
//        handler.handleCommand(cmd, conn);
//
//        verify(conn).pushCommand(isA(InvalidMessageCommand.class));
//        verify(mockConnectionManager).closeConnection(conn);
//    }
//
//    @Test
//    public void ifTheUsernameIsAnonymousThenTheUsernameIsIgnored() {
//        UserAuthService mockAuthService = mock(ConcreteUserAuthService.class);
//        ServerAuthService mockServerAuthService = mock(NetworkManagerService.class);
//        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
//        RemoteServerStateService mockRemoteServerStateService = mock(ConcreteRemoteServerStateService.class);
//
//        BroadcastService broadcastService = mock(BroadcastService.class);
//
//        LoginCommandHandler handler = new LoginCommandHandler(mockAuthService,mockServerAuthService,mockRemoteServerStateService,mockConnectionManager);
//
//        LoginCommand cmd = mock(LoginCommand.class);
//        when(cmd.getUsername()).thenReturn("anonymous");
//
//        Connection conn = mock(Connection.class);
//
//        handler.handleCommand(cmd, conn);
//
//        verify(cmd, never()).getSecret();
//    }
//
  @Test
  public void ifTheUsernameAndSecretAreCachedAndCorrectLoginSuccessCommandIsSent() {
    UserAuthService authService = new ConcreteUserAuthService(
        mock(RemoteServerStateService.class),
        mock(ConnectionManager.class),
        mock(BroadcastService.class)
    );

    Connection mockConnection = mock(Connection.class);

    authService.register("aaron", "harwood", mockConnection);

    ServerAuthService mockServerAuthService = mock(NetworkManagerService.class);
    ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
    RemoteServerStateService mockRemoteServerStateService = mock(ConcreteRemoteServerStateService.class);

    LoginCommandHandler handler = new LoginCommandHandler(
        authService,
        mockServerAuthService,
        mockRemoteServerStateService,
        mockConnectionManager
    );

    LoginCommand command = new LoginCommand("aaron", "harwood");

    handler.handleCommand(command, mockConnection);

    verify(mockConnection).pushCommand(isA(LoginSuccessCommand.class));
  }

  @Test
  public void ifTheUsernameAndSecretAreCachedAndCorrectTheConnectionMustBeLoggedIn() {
    UserAuthService authService = new ConcreteUserAuthService(
        mock(RemoteServerStateService.class),
        mock(ConnectionManager.class),
        mock(BroadcastService.class)
    );

    Connection mockConnection = mock(Connection.class);

    authService.register("aaron", "harwood", mockConnection);

    ServerAuthService mockServerAuthService = mock(NetworkManagerService.class);
    ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
    RemoteServerStateService mockRemoteServerStateService = mock(ConcreteRemoteServerStateService.class);

    LoginCommandHandler handler = new LoginCommandHandler(
        authService,
        mockServerAuthService,
        mockRemoteServerStateService,
        mockConnectionManager
    );

    LoginCommand command = new LoginCommand("aaron", "harwood");

    handler.handleCommand(command, mockConnection);

    verify(mockConnection).pushCommand(isA(LoginSuccessCommand.class));
    assertTrue(authService.isLoggedIn(mockConnection));
  }

  @Test
  public void ifTheUsernameAndSecretAreCachedAndIncorrectSendALoginFailedCommand() {
    UserAuthService authService = new ConcreteUserAuthService(
        mock(RemoteServerStateService.class),
        mock(ConnectionManager.class),
        mock(BroadcastService.class)
    );

    Connection mockConnection = mock(Connection.class);

    authService.register("aaron", "hasabigssd", mockConnection);

    ServerAuthService mockServerAuthService = mock(NetworkManagerService.class);
    ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
    RemoteServerStateService mockRemoteServerStateService = mock(ConcreteRemoteServerStateService.class);

    LoginCommandHandler handler = new LoginCommandHandler(
        authService,
        mockServerAuthService,
        mockRemoteServerStateService,
        mockConnectionManager
    );

    LoginCommand command = new LoginCommand("aaron", "harwood");

    handler.handleCommand(command, mockConnection);

    verify(mockConnection, never()).pushCommand(isA(LoginSuccessCommand.class));
    verify(mockConnection).pushCommand(isA(LoginFailedCommand.class));
    assertFalse(authService.isLoggedIn(mockConnection));
  }

  @Test
  public void testIfTheUsernameIsNotCachedAndTheServerHasAParentThenSendALoginCommandToParent() {
    Connection mockParentConnection = mock(Connection.class);

    ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
    when(mockConnectionManager.hasParent()).thenReturn(true);
    when(mockConnectionManager.getParentConnection()).thenReturn(mockParentConnection);

    UserAuthService authService = new ConcreteUserAuthService(
        mock(RemoteServerStateService.class),
        mockConnectionManager,
        mock(BroadcastService.class)
    );

    Connection mockConnection = mock(Connection.class);

    ServerAuthService mockServerAuthService = mock(NetworkManagerService.class);
    RemoteServerStateService mockRemoteServerStateService = mock(ConcreteRemoteServerStateService.class);

    LoginCommandHandler handler = new LoginCommandHandler(
        authService,
        mockServerAuthService,
        mockRemoteServerStateService,
        mockConnectionManager
    );

    LoginCommand command = new LoginCommand("aaron", "harwood");

    handler.handleCommand(command, mockConnection);

    verify(mockConnection, never()).pushCommand(any(Command.class));
    assertFalse(authService.isLoggedIn(mockConnection));

    verify(mockParentConnection).pushCommand(isA(LoginCommand.class));
  }

  @Test
  public void testSubsequentLoginSuccessFromParentLogsInConnection() {
    Connection mockParentConnection = mock(Connection.class);

    ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
    when(mockConnectionManager.hasParent()).thenReturn(true);
    when(mockConnectionManager.getParentConnection()).thenReturn(mockParentConnection);

    UserAuthService authService = new ConcreteUserAuthService(
        mock(RemoteServerStateService.class),
        mockConnectionManager,
        mock(BroadcastService.class)
    );

    Connection mockConnection = mock(Connection.class);

    ServerAuthService mockServerAuthService = mock(NetworkManagerService.class);
    RemoteServerStateService mockRemoteServerStateService = mock(ConcreteRemoteServerStateService.class);

    LoginCommandHandler handler = new LoginCommandHandler(
        authService,
        mockServerAuthService,
        mockRemoteServerStateService,
        mockConnectionManager
    );

    LoginCommand command = new LoginCommand("aaron", "harwood");

    handler.handleCommand(command, mockConnection);

    verify(mockConnection, never()).pushCommand(any(Command.class));
    assertFalse(authService.isLoggedIn(mockConnection));

    verify(mockParentConnection).pushCommand(isA(LoginCommand.class));

    authService.loginSuccess("aaron", "harwood");
    assertTrue(authService.isLoggedIn(mockConnection));
  }

  @Test
  public void testSubsequentLoginSuccessFromParentLogsInAllClientConnections() {
    Connection mockParentConnection = mock(Connection.class);

    ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
    when(mockConnectionManager.hasParent()).thenReturn(true);
    when(mockConnectionManager.getParentConnection()).thenReturn(mockParentConnection);

    UserAuthService authService = new ConcreteUserAuthService(
        mock(RemoteServerStateService.class),
        mockConnectionManager,
        mock(BroadcastService.class)
    );

    Connection mockConnection = mock(Connection.class);

    ServerAuthService mockServerAuthService = mock(NetworkManagerService.class);
    RemoteServerStateService mockRemoteServerStateService = mock(ConcreteRemoteServerStateService.class);

    LoginCommandHandler handler = new LoginCommandHandler(
        authService,
        mockServerAuthService,
        mockRemoteServerStateService,
        mockConnectionManager
    );

    LoginCommand command = new LoginCommand("aaron", "harwood");

    Connection mockConnection2 = mock(Connection.class);

    when(mockConnectionManager.isServerConnection(mockConnection2)).thenReturn(true);

    handler.handleCommand(command, mockConnection);
    handler.handleCommand(command, mockConnection2);

    verify(mockConnection, never()).pushCommand(any(Command.class));
    assertFalse(authService.isLoggedIn(mockConnection));

    verify(mockParentConnection, times(2)).pushCommand(isA(LoginCommand.class));

    authService.loginSuccess("aaron", "harwood");
    assertTrue(authService.isLoggedIn(mockConnection));
    assertFalse(authService.isLoggedIn(mockConnection2));
  }

  @Test
  public void testSubsequentLoginSuccessFromParentSendsLoginSuccessToAllConnections() {
    Connection mockParentConnection = mock(Connection.class);

    ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
    when(mockConnectionManager.hasParent()).thenReturn(true);
    when(mockConnectionManager.getParentConnection()).thenReturn(mockParentConnection);

    UserAuthService authService = new ConcreteUserAuthService(
        mock(RemoteServerStateService.class),
        mockConnectionManager,
        mock(BroadcastService.class)
    );

    Connection mockConnection = mock(Connection.class);

    ServerAuthService mockServerAuthService = mock(NetworkManagerService.class);
    RemoteServerStateService mockRemoteServerStateService = mock(ConcreteRemoteServerStateService.class);

    LoginCommandHandler handler = new LoginCommandHandler(
        authService,
        mockServerAuthService,
        mockRemoteServerStateService,
        mockConnectionManager
    );

    LoginCommand command = new LoginCommand("aaron", "harwood");

    Connection mockConnection2 = mock(Connection.class);

    handler.handleCommand(command, mockConnection);
    handler.handleCommand(command, mockConnection2);

    verify(mockConnection, never()).pushCommand(any(Command.class));
    assertFalse(authService.isLoggedIn(mockConnection));

    verify(mockParentConnection, times(2)).pushCommand(isA(LoginCommand.class));

    authService.loginSuccess("aaron", "harwood");
    assertTrue(authService.isLoggedIn(mockConnection));
    assertTrue(authService.isLoggedIn(mockConnection2));

    verify(mockConnection).pushCommand(isA(LoginSuccessCommand.class));
    verify(mockConnection2).pushCommand(isA(LoginSuccessCommand.class));
  }

  @Test
  public void testSubsequentLoginFailedFromParentSendsLoginFailedToAllConnections() {
    Connection mockParentConnection = mock(Connection.class);

    ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
    when(mockConnectionManager.hasParent()).thenReturn(true);
    when(mockConnectionManager.getParentConnection()).thenReturn(mockParentConnection);

    UserAuthService authService = new ConcreteUserAuthService(
        mock(RemoteServerStateService.class),
        mockConnectionManager,
        mock(BroadcastService.class)
    );

    Connection mockConnection = mock(Connection.class);

    ServerAuthService mockServerAuthService = mock(NetworkManagerService.class);
    RemoteServerStateService mockRemoteServerStateService = mock(ConcreteRemoteServerStateService.class);

    LoginCommandHandler handler = new LoginCommandHandler(
        authService,
        mockServerAuthService,
        mockRemoteServerStateService,
        mockConnectionManager
    );

    LoginCommand command = new LoginCommand("aaron", "harwood");

    Connection mockConnection2 = mock(Connection.class);

    handler.handleCommand(command, mockConnection);
    handler.handleCommand(command, mockConnection2);

    verify(mockConnection, never()).pushCommand(any(Command.class));
    assertFalse(authService.isLoggedIn(mockConnection));

    verify(mockParentConnection, times(2)).pushCommand(isA(LoginCommand.class));

    authService.loginFailed("aaron", "harwood");

    verify(mockConnection).pushCommand(isA(LoginFailedCommand.class));
    verify(mockConnection2).pushCommand(isA(LoginFailedCommand.class));
  }

  @Test
  public void testIfTheUsernameIsNotCachedAndTheServerIsTheRootThenSendALoginFailed() {
    ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
    when(mockConnectionManager.hasParent()).thenReturn(false);

    UserAuthService authService = new ConcreteUserAuthService(
        mock(RemoteServerStateService.class),
        mockConnectionManager,
        mock(BroadcastService.class)
    );

    Connection mockConnection = mock(Connection.class);

    ServerAuthService mockServerAuthService = mock(NetworkManagerService.class);
    RemoteServerStateService mockRemoteServerStateService = mock(ConcreteRemoteServerStateService.class);

    LoginCommandHandler handler = new LoginCommandHandler(
        authService,
        mockServerAuthService,
        mockRemoteServerStateService,
        mockConnectionManager
    );

    LoginCommand command = new LoginCommand("aaron", "harwood");

    handler.handleCommand(command, mockConnection);

    assertFalse(authService.isLoggedIn(mockConnection));

    verify(mockConnection).pushCommand(isA(LoginFailedCommand.class));
  }
//
//    @Test
//    public void ifThereIsAServerToRedirectToThenARedirectCommandIsSent() {
//        UserAuthService mockAuthService = mock(ConcreteUserAuthService.class);
//        when(mockAuthService.isUserRegistered(anyString(), anyString())).thenReturn(true);
//        when(mockAuthService.login(anyString(), anyString(), any(Connection.class))).thenReturn(true);
//
//        ServerAuthService mockServerAuthService = mock(NetworkManagerService.class);
//        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
//        RemoteServerStateService mockRemoteServerStateService = mock(ConcreteRemoteServerStateService.class);
//
//        BroadcastService broadcastService = mock(BroadcastService.class);
//
//        LoginCommandHandler handler = new LoginCommandHandler(mockAuthService,mockServerAuthService,mockRemoteServerStateService,mockConnectionManager);
//
//        LoginCommand cmd = mock(LoginCommand.class);
//        when(cmd.getUsername()).thenReturn("username");
//        when(cmd.getSecret()).thenReturn("password");
//
//        Connection conn = mock(Connection.class);
//
//        handler.handleCommand(cmd, conn);
//        verify(mockRemoteServerStateService).loadBalance(conn);
//    }
//
//    @Test
//    public void ifTheUserIsNotRegisteredALoginFailedCommandIsSent() {
//        UserAuthService mockAuthService = mock(ConcreteUserAuthService.class);
//        when(mockAuthService.isUserRegistered(anyString(), anyString())).thenReturn(false);
//        when(mockAuthService.login(anyString(), anyString(), any(Connection.class))).thenReturn(false);
//
//        ServerAuthService mockServerAuthService = mock(NetworkManagerService.class);
//        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
//        RemoteServerStateService mockRemoteServerStateService = mock(ConcreteRemoteServerStateService.class);
//
//        BroadcastService broadcastService = mock(BroadcastService.class);
//
//        LoginCommandHandler handler = new LoginCommandHandler(mockAuthService,mockServerAuthService,mockRemoteServerStateService,mockConnectionManager);
//
//        LoginCommand cmd = mock(LoginCommand.class);
//        when(cmd.getUsername()).thenReturn("username");
//        when(cmd.getSecret()).thenReturn("password");
//
//        Connection conn = mock(Connection.class);
//
//        handler.handleCommand(cmd, conn);
//        verify(mockRemoteServerStateService, never()).loadBalance(conn);
//        verify(conn).pushCommand(isA(LoginFailedCommand.class));
//    }
}
