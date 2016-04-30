package activitystreamer.core.shared;

import activitystreamer.core.command.Command;
import activitystreamer.core.command.InvalidMessageCommand;
import activitystreamer.core.command.transmission.CommandDeserializer;
import activitystreamer.core.command.transmission.CommandParseException;
import activitystreamer.core.command.transmission.CommandSerializer;
import activitystreamer.core.commandprocessor.CommandProcessor;
import activitystreamer.util.Settings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;

public class Connection implements Closeable, Runnable {
    private static final Logger log = LogManager.getLogger();
    private BufferedReader in;
    private PrintWriter out;
    private boolean open = false;
    private Socket socket;
    private boolean term = false;
    private boolean isRunning = true;

    private CommandSerializer commandSerializer;
    private CommandDeserializer commandDeserializer;
    private CommandProcessor processor;
    private DisconnectHandler disconnectHandler;

    public Connection(Socket socket,
                      CommandSerializer commandSerializer,
                      CommandDeserializer commandDeserializer,
                      CommandProcessor processor,
                      DisconnectHandler disconnectHandler) throws IOException {
        this.socket = socket;
        this.commandSerializer = commandSerializer;
        this.commandDeserializer = commandDeserializer;
        this.processor = processor;
        this.disconnectHandler = disconnectHandler;

        in = new BufferedReader(new InputStreamReader(new DataInputStream(socket.getInputStream())));
        out = new PrintWriter(new DataOutputStream(socket.getOutputStream()), true);
        open = true;
    }

    @Override
    public void run() {
        while (!term) {
            try {
                Command cmd = pullCommand();
                log.info("Deserialized command: " + cmd);
                if (cmd != null) {
                    processor.processCommandIncoming(this, cmd);
                }
            } catch (IOException e) {
                log.error("I/O exception. Closing connection");
                disconnectHandler.closeConnection(this);
                term = true;
            } catch (CommandParseException e) {
                log.error("Invalid message. Closing connection.");
                Command cmd = new InvalidMessageCommand("Invalid message format.");
                this.pushCommand(cmd);
                disconnectHandler.closeConnection(this);
                term = true;
            }
        }
        isRunning = false;
    }
    
    public void pushCommand(Command cmd) {
        String message = commandSerializer.serialize(cmd);
        log.info("Sent message: " + message);
        this.writeLine(message);
    }

    private Command pullCommand() throws IOException, CommandParseException {
        String message = this.readLine();

        if (message == null) {
            throw new IOException();
        }

        log.info("Received message: " + message);
        return commandDeserializer.deserialize(message);
    }

    private String readLine() throws IOException {
        if (!open) {
            throw new IOException("Connection is closed.");
        }
        return in.readLine();
    }

    private boolean writeLine(String msg) {
        if (open) {
            out.println(msg);
            out.flush();
            return true;
        }
        return false;
    }

    public synchronized void setCommandProcessor(CommandProcessor processor) {
        this.processor = processor;
    }

    public CommandProcessor getCommandProcessor() {
        return this.processor;
    }

    public boolean getIsRunning() {
        return isRunning;
    }

    @Override
    public synchronized void close() {
        if (open) {
            log.info("closing connection " + Settings.socketAddress(socket));
            try {
                open = false;
                term = true;
                in.close();
                out.close();
            } catch (IOException e) {
                log.error("received exception closing the connection " + Settings.socketAddress(socket) + ": " + e);
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public synchronized boolean isOpen() {
        return open;
    }
}
