package activitystreamer.server.services;

import activitystreamer.core.command.LockAllowedCommand;
import activitystreamer.core.command.LockDeniedCommand;
import activitystreamer.core.command.RegisterFailedCommand;
import activitystreamer.core.command.RegisterSuccessCommand;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.BroadcastService;
import activitystreamer.server.services.contracts.ConnectionManager;
import activitystreamer.server.services.impl.ConcreteUserAuthService;
import org.junit.Test;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class ConcreteUserAuthServiceTest {
    @Test
    public void testIfRegisterCalledAtRootRegisterImmediately() {

        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
        when(mockConnectionManager.hasParent()).thenReturn(false);
        when(mockConnectionManager.getParentConnection()).thenReturn(mock(Connection.class));

        ConcreteUserAuthService authService = new ConcreteUserAuthService(
            mockConnectionManager,
            mock(BroadcastService.class)
        );

        assertFalse(authService.isUserRegistered("aaron", "isthebest"));
        authService.register("aaron", "isthebest", mock(Connection.class));
        assertTrue(authService.isUserRegistered("aaron", "isthebest"));
    }

    @Test
    public void testIfRegisterCalledWithParentWaitForRegisterSuccess() {

        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
        when(mockConnectionManager.hasParent()).thenReturn(true);
        when(mockConnectionManager.getParentConnection()).thenReturn(mock(Connection.class));

        ConcreteUserAuthService authService = new ConcreteUserAuthService(
            mockConnectionManager,
            mock(BroadcastService.class)
        );

        assertFalse(authService.isUserRegistered("aaron", "isthebest"));
        authService.register("aaron", "isthebest", mock(Connection.class));
        assertFalse(authService.isUserRegistered("aaron", "isthebest"));
    }

    @Test
    public void ifPendingRegistrationAndRegisterSuccessCalledThenRegisterUser() {
        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
        when(mockConnectionManager.hasParent()).thenReturn(true);
        when(mockConnectionManager.getParentConnection()).thenReturn(mock(Connection.class));

        ConcreteUserAuthService authService = new ConcreteUserAuthService(
            mockConnectionManager,
            mock(BroadcastService.class)
        );

        authService.register("aaron", "isthebest", mock(Connection.class));

        assertFalse(authService.isUserRegistered("aaron", "isthebest"));

        authService.registerSuccess("aaron", "isthebest");

        assertTrue(authService.isUserRegistered("aaron", "isthebest"));
    }

    @Test
    public void testSendRegisterSuccessToReplyConnection() {
        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
        when(mockConnectionManager.hasParent()).thenReturn(true);
        when(mockConnectionManager.getParentConnection()).thenReturn(mock(Connection.class));

        ConcreteUserAuthService authService = new ConcreteUserAuthService(
            mockConnectionManager,
            mock(BroadcastService.class)
        );

        Connection replyConnection = mock(Connection.class);

        authService.register("aaron", "isthebest", replyConnection);

        assertFalse(authService.isUserRegistered("aaron", "isthebest"));

        authService.registerSuccess("aaron", "isthebest");

        assertTrue(authService.isUserRegistered("aaron", "isthebest"));

        verify(replyConnection).pushCommand(isA(RegisterSuccessCommand.class));
    }

    @Test
    public void testIgnoreUnexpectedRegisterSuccess() {
        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
        when(mockConnectionManager.hasParent()).thenReturn(true);
        when(mockConnectionManager.getParentConnection()).thenReturn(mock(Connection.class));

        ConcreteUserAuthService authService = new ConcreteUserAuthService(
            mockConnectionManager,
            mock(BroadcastService.class)
        );

        Connection replyConnection = mock(Connection.class);

        assertFalse(authService.isUserRegistered("aaron", "isthebest"));

        authService.registerSuccess("aaron", "isthebest");

        assertFalse(authService.isUserRegistered("aaron", "isthebest"));

        verify(replyConnection, never()).pushCommand(isA(RegisterSuccessCommand.class));
    }

    @Test
    public void testIfRegisterSuccessPasswordDoesNotMatchDontRegisterTheUser() {
        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
        when(mockConnectionManager.hasParent()).thenReturn(true);
        when(mockConnectionManager.getParentConnection()).thenReturn(mock(Connection.class));

        ConcreteUserAuthService authService = new ConcreteUserAuthService(
            mockConnectionManager,
            mock(BroadcastService.class)
        );

        Connection replyConnection = mock(Connection.class);

        authService.register("aaron", "isthebest", replyConnection);

        assertFalse(authService.isUserRegistered("aaron", "isthebest"));

        authService.registerSuccess("aaron", "hasabigssd");

        assertFalse(authService.isUserRegistered("aaron", "isthebest"));
        assertFalse(authService.isUserRegistered("aaron", "hasabigssd"));

        verify(replyConnection, never()).pushCommand(isA(RegisterSuccessCommand.class));
    }

    @Test
    public void testSendLockAllowedToLegacyServerReplyConnection() {
        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
        when(mockConnectionManager.hasParent()).thenReturn(true);
        when(mockConnectionManager.getParentConnection()).thenReturn(mock(Connection.class));

        ConcreteUserAuthService authService = new ConcreteUserAuthService(
            mockConnectionManager,
            mock(BroadcastService.class)
        );

        Connection replyConnection = mock(Connection.class);
        when(mockConnectionManager.isLegacyServer(replyConnection)).thenReturn(true);

        authService.register("aaron", "isthebest", replyConnection);

        assertFalse(authService.isUserRegistered("aaron", "isthebest"));

        authService.registerSuccess("aaron", "isthebest");

        assertTrue(authService.isUserRegistered("aaron", "isthebest"));

        verify(replyConnection, never()).pushCommand(isA(RegisterSuccessCommand.class));
        verify(replyConnection).pushCommand(isA(LockAllowedCommand.class));
    }

    @Test
    public void testRegisterFailedForwardCommandToReplyConnection() {
        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
        when(mockConnectionManager.hasParent()).thenReturn(true);
        when(mockConnectionManager.getParentConnection()).thenReturn(mock(Connection.class));

        ConcreteUserAuthService authService = new ConcreteUserAuthService(
            mockConnectionManager,
            mock(BroadcastService.class)
        );

        Connection replyConnection = mock(Connection.class);

        authService.register("aaron", "isthebest", replyConnection);
        authService.registerFailed("aaron", "isthebest");

        verify(replyConnection).pushCommand(isA(RegisterFailedCommand.class));
    }

    @Test
    public void testRegisterFailedSendLockDeniedToLegacyServerReplyConnection() {
        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
        when(mockConnectionManager.hasParent()).thenReturn(true);
        when(mockConnectionManager.getParentConnection()).thenReturn(mock(Connection.class));

        ConcreteUserAuthService authService = new ConcreteUserAuthService(
            mockConnectionManager,
            mock(BroadcastService.class)
        );

        Connection replyConnection = mock(Connection.class);
        when(mockConnectionManager.isLegacyServer(replyConnection)).thenReturn(true);

        authService.register("aaron", "isthebest", replyConnection);
        authService.registerFailed("aaron", "isthebest");

        verify(replyConnection, never()).pushCommand(isA(RegisterFailedCommand.class));
        verify(replyConnection).pushCommand(isA(LockDeniedCommand.class));
    }

    @Test
    public void testIgnoreUnexpectedRegisterFailed() {
        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
        when(mockConnectionManager.hasParent()).thenReturn(true);
        when(mockConnectionManager.getParentConnection()).thenReturn(mock(Connection.class));

        ConcreteUserAuthService authService = new ConcreteUserAuthService(
            mockConnectionManager,
            mock(BroadcastService.class)
        );

        Connection replyConnection = mock(Connection.class);
        when(mockConnectionManager.isLegacyServer(replyConnection)).thenReturn(true);

        authService.registerFailed("aaron", "isthebest");

        verify(replyConnection, never()).pushCommand(isA(RegisterFailedCommand.class));
        verify(replyConnection, never()).pushCommand(isA(LockDeniedCommand.class));
    }

    @Test
    public void testIfReplyConnectionToRegisterFailedIsClientThenCloseConnection() {
        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
        when(mockConnectionManager.hasParent()).thenReturn(true);
        when(mockConnectionManager.getParentConnection()).thenReturn(mock(Connection.class));

        ConcreteUserAuthService authService = new ConcreteUserAuthService(
            mockConnectionManager,
            mock(BroadcastService.class)
        );

        Connection replyConnection = mock(Connection.class);
        when(mockConnectionManager.isLegacyServer(replyConnection)).thenReturn(false);
        when(mockConnectionManager.isClientConnection(replyConnection)).thenReturn(true);

        authService.register("aaron", "isthebest", replyConnection);
        authService.registerFailed("aaron", "isthebest");

        verify(mockConnectionManager).closeConnection(replyConnection);
    }
}
