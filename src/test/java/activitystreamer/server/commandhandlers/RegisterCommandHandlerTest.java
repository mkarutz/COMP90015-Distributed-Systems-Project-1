package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.RemoteServerStateService;
import activitystreamer.server.services.UserAuthService;
import com.google.gson.JsonObject;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RegisterCommandHandlerTest {
    @Test
    public void testUsernameMustBeNotNull() {
        UserAuthService mockAuthService = mock(UserAuthService.class);
        RegisterCommandHandler handler = new RegisterCommandHandler(mockAuthService);

        RegisterCommand mockCommand = mock(RegisterCommand.class);
        when(mockCommand.getUsername()).thenReturn(null);

        Connection mockConnection = mock(Connection.class);

        handler.handleCommandIncoming(mockCommand, mockConnection);

        verify(mockConnection).pushCommand(isA(InvalidMessageCommand.class));
    }

    @Test
    public void testSecretMustBeNotNull() {
        UserAuthService mockAuthService = mock(UserAuthService.class);
        RegisterCommandHandler handler = new RegisterCommandHandler(mockAuthService);

        RegisterCommand mockCommand = mock(RegisterCommand.class);
        when(mockCommand.getUsername()).thenReturn("username");
        when(mockCommand.getSecret()).thenReturn(null);

        Connection mockConnection = mock(Connection.class);

        handler.handleCommandIncoming(mockCommand, mockConnection);

        verify(mockConnection).pushCommand(isA(InvalidMessageCommand.class));
    }

    @Test
    public void testRegisterIsCalledOnTheAuthService() {
        UserAuthService mockAuthService = mock(UserAuthService.class);
        when(mockAuthService.register(anyString(), anyString(), any(Connection.class))).thenReturn(false);

        RegisterCommandHandler handler = new RegisterCommandHandler(mockAuthService);

        RegisterCommand mockCommand = mock(RegisterCommand.class);
        when(mockCommand.getUsername()).thenReturn("username");
        when(mockCommand.getSecret()).thenReturn("password");

        Connection mockConnection = mock(Connection.class);

        handler.handleCommandIncoming(mockCommand, mockConnection);

        verify(mockAuthService).register(mockCommand.getUsername(), mockCommand.getSecret(), mockConnection);
    }

    @Test
    public void testIfTheUsernameIsAlreadyKnownThenSendARegisterFailedCommand() {
        UserAuthService mockAuthService = mock(UserAuthService.class);
        when(mockAuthService.register(anyString(), anyString(), any(Connection.class))).thenReturn(false);

        RegisterCommandHandler handler = new RegisterCommandHandler(mockAuthService);

        RegisterCommand mockCommand = mock(RegisterCommand.class);
        when(mockCommand.getUsername()).thenReturn("username");
        when(mockCommand.getSecret()).thenReturn("password");

        Connection mockConnection = mock(Connection.class);

        handler.handleCommandIncoming(mockCommand, mockConnection);

        verify(mockConnection).pushCommand(isA(RegisterFailedCommand.class));
    }
}
