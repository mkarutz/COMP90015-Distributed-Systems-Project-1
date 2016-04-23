package activitystreamer.server;

import java.io.*;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import activitystreamer.util.Settings;
import activitystreamer.CommandAdapter;

import activitystreamer.core.command.*;
import activitystreamer.core.commandprocessor.*;

import com.google.gson.*;

public class Connection implements Closeable {
    private static final Logger log = LogManager.getLogger();
    private BufferedReader in;
    private PrintWriter out;
    private boolean open = false;
    private Socket socket;
    private boolean term = false;

    private ICommandProcessor processor;
    private Gson              gson;

    Connection(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(new DataInputStream(socket.getInputStream())));
        out = new PrintWriter(new DataOutputStream(socket.getOutputStream()), true);
        open = true;

        // Start connection using a pending command processor
        this.processor = new PendingCommandProcessor(this);

        // Init gson parser
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ICommand.class, new CommandAdapter())
                .create();
    }

    // Process connection
    public void process() {
        ICommand next;
        while ((next = this.pullCommand()) != null) {
            // There's a command to process, process it via command processor
            this.processor.processCommand(next);
        }
    }

    // Send a command upstream
    public void pushCommand(ICommand cmd) {
        String json = this.gson.toJson(cmd, ICommand.class);
        this.writeLine(json);
    }

    // Receive a command downstream (or null if none)
    private ICommand pullCommand() {
        try {
            String line = this.readLine();
            if (line != null) {
                ICommand json = gson.fromJson(line, ICommand.class);
                return json;
            }
        } catch (IOException e) {
            log.error("bad downstream data exception: " + e);
            this.close();
        }
        return null;
    }

    // Read raw line from connection
    private String readLine() throws IOException {
        if (!open) {
            return null;
        }
        return in.readLine();
    }

    // Write raw line to connection
    private boolean writeLine(String msg) {
        if (open) {
            out.println(msg);
            out.flush();
            return true;
        }
        return false;
    }

    @Override
    public synchronized void close() {
        if (open) {
            log.info("closing connection " + Settings.socketAddress(socket));
            try {
                open = false;
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
