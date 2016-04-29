package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.ServerAuthService;
import activitystreamer.server.services.contracts.UserAuthService;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class LockRequestCommandHandlerTest {
    @Test
    public void testUsernameMustBeNotNull() {
        ServerAuthService mockServerAuthService = mock(activitystreamer.server.services.impl.ServerAuthService.class);
        UserAuthService mockAuthService = mock(activitystreamer.server.services.impl.UserAuthService.class);

        LockRequestCommandHandler handler = new LockRequestCommandHandler(
                mockAuthService,
                mockServerAuthService
        );

        LockRequestCommand mockCommand = mock(LockRequestCommand.class);
        when(mockCommand.getUsername()).thenReturn(null);
        when(mockCommand.getSecret()).thenReturn("password");

        Connection mockConnection = mock(Connection.class);

        handler.handleCommand(mockCommand, mockConnection);

        verify(mockConnection).pushCommand(isA(InvalidMessageCommand.class));
    }

    @Test
    public void testSecretMustBeNotNull() {
        ServerAuthService mockServerAuthService = mock(activitystreamer.server.services.impl.ServerAuthService.class);
        UserAuthService mockAuthService = mock(activitystreamer.server.services.impl.UserAuthService.class);

        LockRequestCommandHandler handler = new LockRequestCommandHandler(
                mockAuthService,
                mockServerAuthService
        );

        LockRequestCommand mockCommand = mock(LockRequestCommand.class);
        when(mockCommand.getUsername()).thenReturn("username");
        when(mockCommand.getSecret()).thenReturn(null);

        Connection mockConnection = mock(Connection.class);

        handler.handleCommand(mockCommand, mockConnection);

        verify(mockConnection).pushCommand(isA(InvalidMessageCommand.class));
    }

    @Test
    public void testIfTheServerHasNotAuthenticatedThenSendInvalidMessageCommand() {
        ServerAuthService mockServerAuthService = mock(ServerAuthService.class);
        UserAuthService mockAuthService = mock(UserAuthService.class);

        LockRequestCommandHandler handler = new LockRequestCommandHandler(
                mockAuthService,
                mockServerAuthService
        );

        LockRequestCommand mockCommand = mock(LockRequestCommand.class);
        when(mockCommand.getUsername()).thenReturn("username");
        when(mockCommand.getSecret()).thenReturn(null);

        Connection mockConnection = mock(Connection.class);

        handler.handleCommand(mockCommand, mockConnection);

        verify(mockConnection).pushCommand(isA(InvalidMessageCommand.class));
    }

//    @Test
//    public void testIfTheUsernameIsAlreadyKnownThenBroadcastLockDeniedCommand() {
//        UserAuthService mockAuthService = mock(UserAuthService.class);
//
//        ICommandBroadcaster mockBroadcastService = mock(ICommandBroadcaster.class);
//
//        LockRequestCommandHandler handler
//                = new LockRequestCommandHandler(mockAuthService, mockBroadcastService);
//
//        LockRequestCommand mockCommand = mock(LockRequestCommand.class);
//        when(mockCommand.getUsername()).thenReturn("username");
//        when(mockCommand.getSecret()).thenReturn("password");
//
//        Connection mockConnection = mock(Connection.class);
//
//        handler.handleCommand(mockCommand, mockConnection);
//
//        verify(mockConnection).pushCommand(isA(InvalidMessageCommand.class));
//    }
}
