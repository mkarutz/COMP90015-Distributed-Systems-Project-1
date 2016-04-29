package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.AuthenticationFailCommand;
import activitystreamer.core.command.ICommand;
import activitystreamer.core.command.InvalidMessageCommand;
import activitystreamer.core.shared.*;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import activitystreamer.server.services.ConnectionStateService;

public class AuthenticationFailCommandHandlerTest {
    //AuthenticationFailCommand closes connection anyway so unnecessary to check source
    // @Test
    // public void ifClientSendInvalid() {
    //     ConnectionStateService mockConnectionStateService=mock(ConnectionStateService.class);
    //     when(mockConnectionStateService.getConnectionType(any(Connection.class))).thenReturn(ConnectionStateService.ConnectionType.CLIENT);
    //
    //     AuthenticationFailCommandHandler handler = new AuthenticationFailCommandHandler(mockConnectionStateService);
    //
    //     ICommand mockCommand = spy(new AuthenticationFailCommand("Incorrect secret."));
    //     Connection mockConnection = mock(Connection.class);
    //
    //     handler.handleCommand(mockCommand, mockConnection);
    //
    //     verify(mockConnection).pushCommand(isA(InvalidMessageCommand.class));
    //     verify(mockConnection).close();
    // }

    @Test
    public void testConnectionIsClosed() {
        // ConnectionStateService mockConnectionStateService=mock(ConnectionStateService.class);
        // when(mockConnectionStateService.getConnectionType(any(Connection.class))).thenReturn(ConnectionStateService.ConnectionType.SERVER);

        AuthenticationFailCommandHandler handler = new AuthenticationFailCommandHandler();

        ICommand mockCommand = spy(new AuthenticationFailCommand("Incorrect secret."));
        Connection mockConnection = mock(Connection.class);

        handler.handleCommand(mockCommand, mockConnection);

        verify(mockConnection).close();
    }
}
