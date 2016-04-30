package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.AuthenticationFailCommand;
import activitystreamer.core.command.Command;
import activitystreamer.core.shared.*;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class AuthenticationFailCommandHandlerTest {
    @Test
    public void testConnectionIsClosed() {
        AuthenticationFailCommandHandler handler = new AuthenticationFailCommandHandler();

        Command mockCommand = spy(new AuthenticationFailCommand("Incorrect secret."));
        Connection mockConnection = mock(Connection.class);

        handler.handleCommand(mockCommand, mockConnection);

        verify(mockConnection).close();
    }
}
