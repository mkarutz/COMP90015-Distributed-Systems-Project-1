package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.ServerState;
import activitystreamer.server.services.ConnectionStateService;
import activitystreamer.server.services.RemoteServerStateService;
import activitystreamer.server.services.ServerAuthService;
import activitystreamer.server.services.UserAuthService;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class AuthenticateCommandHandlerTest {
    @Test
    public void testIfThereIsNoSecretThenSendAnInvalidMessageCommand() {
        RemoteServerStateService mockServerStateService = mock(RemoteServerStateService.class);
        UserAuthService mockAuthService = mock(UserAuthService.class);
        ServerAuthService mockServerAuthService = mock(ServerAuthService.class);
        ConnectionStateService mockConnectionStateService = mock(ConnectionStateService.class);

        AuthenticateCommandHandler handler = new AuthenticateCommandHandler(
                mockServerStateService,
                mockAuthService,
                mockServerAuthService,
                mockConnectionStateService
        );

        AuthenticateCommand mockCommand = mock(AuthenticateCommand.class);
        when(mockCommand.getSecret()).thenReturn(null);

        Connection mockConnection = mock(Connection.class);
        handler.handleCommand(mockCommand, mockConnection);

        verify(mockConnection).pushCommand(isA(InvalidMessageCommand.class));
        verify(mockConnection).close();
    }
}
