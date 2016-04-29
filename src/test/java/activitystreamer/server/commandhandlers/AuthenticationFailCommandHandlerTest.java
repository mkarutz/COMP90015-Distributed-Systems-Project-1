package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.AuthenticationFailCommand;
import activitystreamer.core.command.ICommand;
import activitystreamer.core.shared.*;
import org.junit.Test;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class AuthenticationFailCommandHandlerTest {
    @Test
    public void testConnectionIsClosed() {
        AuthenticationFailCommandHandler handler = new AuthenticationFailCommandHandler();

        ICommand mockCommand = spy(new AuthenticationFailCommand("Incorrect secret."));
        Connection mockConnection = mock(Connection.class);

        handler.handleCommand(mockCommand, mockConnection);

        verify(mockConnection).close();
    }
}
