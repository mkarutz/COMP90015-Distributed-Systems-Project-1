package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.InvalidMessageCommand;
import activitystreamer.core.commandhandler.InvalidMessageCommandHandler;
import activitystreamer.core.shared.Connection;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class InvalidMessageCommandHandlerTest {
    @Test
    public void testTheConnectionIsClosed() {
        InvalidMessageCommandHandler handler = new InvalidMessageCommandHandler();

        Connection mockConnection = mock(Connection.class);
        InvalidMessageCommand mockCommand = mock(InvalidMessageCommand.class);
        handler.handleCommand(mockCommand, mockConnection);

        verify(mockConnection).close();
    }
}
