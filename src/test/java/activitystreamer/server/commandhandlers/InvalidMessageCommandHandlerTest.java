package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.InvalidMessageCommand;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.contracts.ConnectionManager;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class InvalidMessageCommandHandlerTest {
    @Test
    public void testTheConnectionIsClosed() {
        ConnectionManager mockConnectionManager = mock(ConnectionManager.class);
        InvalidMessageCommandHandler handler = new InvalidMessageCommandHandler(mockConnectionManager);

        Connection mockConnection = mock(Connection.class);
        InvalidMessageCommand mockCommand = mock(InvalidMessageCommand.class);
        handler.handleCommand(mockCommand, mockConnection);

        verify(mockConnectionManager).closeConnection(mockConnection);
    }
}
