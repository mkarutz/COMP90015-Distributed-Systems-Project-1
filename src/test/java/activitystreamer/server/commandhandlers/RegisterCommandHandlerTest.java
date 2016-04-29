package activitystreamer.server.commandhandlers;

import activitystreamer.core.command.*;
import activitystreamer.core.shared.Connection;
import activitystreamer.server.services.RemoteServerStateService;
import activitystreamer.server.services.UserAuthService;
import com.google.gson.JsonObject;
import org.junit.Test;
import activitystreamer.server.services.ConnectionStateService;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RegisterCommandHandlerTest {
    @Test
    public void testUsernameMustBeNotNull() {
        UserAuthService mockAuthService = mock(UserAuthService.class);
        ConnectionStateService mockConnectionStateService=mock(ConnectionStateService.class);
        when(mockConnectionStateService.getConnectionType(any(Connection.class))).thenReturn(ConnectionStateService.ConnectionType.UNKNOWN);
        RegisterCommandHandler handler = new RegisterCommandHandler(mockAuthService,mockConnectionStateService);

        RegisterCommand mockCommand = mock(RegisterCommand.class);
        when(mockCommand.getUsername()).thenReturn(null);

        Connection mockConnection = mock(Connection.class);

        handler.handleCommand(mockCommand, mockConnection);

        verify(mockConnection).pushCommand(isA(InvalidMessageCommand.class));
    }

    @Test
    public void testSecretMustBeNotNull() {
        UserAuthService mockAuthService = mock(UserAuthService.class);
        ConnectionStateService mockConnectionStateService=mock(ConnectionStateService.class);
        when(mockConnectionStateService.getConnectionType(any(Connection.class))).thenReturn(ConnectionStateService.ConnectionType.UNKNOWN);
        RegisterCommandHandler handler = new RegisterCommandHandler(mockAuthService,mockConnectionStateService);


        RegisterCommand mockCommand = mock(RegisterCommand.class);
        when(mockCommand.getUsername()).thenReturn("username");
        when(mockCommand.getSecret()).thenReturn(null);

        Connection mockConnection = mock(Connection.class);

        handler.handleCommand(mockCommand, mockConnection);

        verify(mockConnection).pushCommand(isA(InvalidMessageCommand.class));
    }

    @Test
    public void testRegisterIsCalledOnTheAuthService() {
        UserAuthService mockAuthService = mock(UserAuthService.class);
        when(mockAuthService.register(anyString(), anyString(), any(Connection.class))).thenReturn(false);

        ConnectionStateService mockConnectionStateService=mock(ConnectionStateService.class);
        when(mockConnectionStateService.getConnectionType(any(Connection.class))).thenReturn(ConnectionStateService.ConnectionType.UNKNOWN);
        RegisterCommandHandler handler = new RegisterCommandHandler(mockAuthService,mockConnectionStateService);

        RegisterCommand mockCommand = mock(RegisterCommand.class);
        when(mockCommand.getUsername()).thenReturn("username");
        when(mockCommand.getSecret()).thenReturn("password");

        Connection mockConnection = mock(Connection.class);

        handler.handleCommand(mockCommand, mockConnection);

        verify(mockAuthService).register(mockCommand.getUsername(), mockCommand.getSecret(), mockConnection);
    }

    @Test
    public void testIfTheUsernameIsAlreadyKnownThenSendARegisterFailedCommand() {
        UserAuthService mockAuthService = mock(UserAuthService.class);
        when(mockAuthService.register(anyString(), anyString(), any(Connection.class))).thenReturn(false);

        ConnectionStateService mockConnectionStateService=mock(ConnectionStateService.class);
        when(mockConnectionStateService.getConnectionType(any(Connection.class))).thenReturn(ConnectionStateService.ConnectionType.UNKNOWN);
        RegisterCommandHandler handler = new RegisterCommandHandler(mockAuthService,mockConnectionStateService);

        RegisterCommand mockCommand = mock(RegisterCommand.class);
        when(mockCommand.getUsername()).thenReturn("username");
        when(mockCommand.getSecret()).thenReturn("password");

        Connection mockConnection = mock(Connection.class);

        handler.handleCommand(mockCommand, mockConnection);

        verify(mockConnection).pushCommand(isA(RegisterFailedCommand.class));
    }
}
