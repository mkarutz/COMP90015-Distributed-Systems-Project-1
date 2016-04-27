package activitystreamer.client.commandhandlers;

import activitystreamer.core.command.RedirectCommand;
import activitystreamer.core.shared.Connection;
import activitystreamer.client.ClientSolution;
import activitystreamer.util.Settings;
import org.junit.Test;

import java.net.InetAddress;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class RedirectCommandHandlerTest {
    @Test
    public void testConnectionIsClosed() {
        ClientSolution mockClientSolution = mock(ClientSolution.class);
        RedirectCommandHandler handler = new RedirectCommandHandler(mockClientSolution);

        Connection mockConnection = mock(Connection.class);

        InetAddress mockAddress = mock(InetAddress.class);
        when(mockAddress.getHostName()).thenReturn("www.google.com");
        RedirectCommand command = new RedirectCommand(mockAddress, 1234);

        handler.handleCommandIncoming(command, mockConnection);

        verify(mockConnection).close();
    }

    @Test
    public void testSettingsAreUpdated() {
        ClientSolution mockClientSolution = mock(ClientSolution.class);
        RedirectCommandHandler handler = new RedirectCommandHandler(mockClientSolution);

        Settings.setRemoteHostname("www.not-google.com");
        Settings.setRemotePort(4321);

        InetAddress mockAddress = mock(InetAddress.class);
        when(mockAddress.getHostName()).thenReturn("www.google.com");

        int port = 1234;

        Connection mockConnection = mock(Connection.class);
        RedirectCommand mockCommand = mock(RedirectCommand.class);
        when(mockCommand.getHostname()).thenReturn(mockAddress);
        when(mockCommand.getPort()).thenReturn(port);

        handler.handleCommandIncoming(mockCommand, mockConnection);

        assertEquals(Settings.getRemoteHostname(), mockAddress.getHostName());
        assertEquals(Settings.getRemotePort(), port);
    }

    @Test
    public void testNewConnectionIsInitialised() {
        ClientSolution mockClientSolution = mock(ClientSolution.class);
        RedirectCommandHandler handler = new RedirectCommandHandler(mockClientSolution);

        Connection mockConnection = mock(Connection.class);

        InetAddress mockAddress = mock(InetAddress.class);
        when(mockAddress.getHostName()).thenReturn("www.google.com");
        RedirectCommand command = new RedirectCommand(mockAddress, 1234);

        handler.handleCommandIncoming(command, mockConnection);

        verify(mockClientSolution).initiateConnection();
    }
}
