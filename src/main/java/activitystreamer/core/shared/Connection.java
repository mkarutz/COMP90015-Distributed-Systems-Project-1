package activitystreamer.core.shared;

import java.io.*;
import java.net.Socket;

import activitystreamer.core.command.transmission.CommandDeserializer;
import activitystreamer.core.command.transmission.CommandParseException;
import activitystreamer.core.command.transmission.CommandSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import activitystreamer.util.Settings;

import activitystreamer.core.command.*;
import activitystreamer.core.commandprocessor.*;

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

    public Connection(Socket socket,
                      CommandSerializer commandSerializer,
                      CommandDeserializer commandDeserializer,
                      CommandProcessor processor) throws IOException {
        this.socket = socket;
        this.commandSerializer = commandSerializer;
        this.commandDeserializer = commandDeserializer;
        this.processor = processor;

        in = new BufferedReader(new InputStreamReader(new DataInputStream(socket.getInputStream())));
        out = new PrintWriter(new DataOutputStream(socket.getOutputStream()), true);
        open = true;
    }

    @Override
    public void run() {
        while (!term) {
            try {
                ICommand cmd = pullCommand();
                if (cmd != null) {
                    processor.processCommandIncoming(this, cmd);
                }
            } catch (IOException e) {
                log.error("I/O exception. Closing connection");
                term = true;
            } catch (CommandParseException e) {
                log.error("Invalid message. Closing connection.");
                ICommand cmd = new InvalidMessageCommand(e.getMessage());
                this.pushCommand(cmd);
                term = true;
            }
        }
        isRunning = false;
    }

    public void pushCommand(ICommand cmd) {
        String message = commandSerializer.serialize(cmd);
        log.info("Sent message: " + message);
        this.writeLine(message);
    }

    private ICommand pullCommand() throws IOException, CommandParseException {
        String message = this.readLine();

        if (message == null) {
            close();
            return null;
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

    // testing Bad msgs
    // public void writeBad(String msg){
    //     this.writeLine(msg);
    // }
}
