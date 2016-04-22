package activitystreamer.server;

import java.io.*;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import activitystreamer.util.Settings;

//added for pushCommand
import activitystreamer.core.command.*;

public class Connection implements Closeable {
    private static final Logger log = LogManager.getLogger();
    private BufferedReader in;
    private PrintWriter out;
    private boolean open = false;
    private Socket socket;
    private boolean term = false;

    Connection(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(new DataInputStream(socket.getInputStream())));
        out = new PrintWriter(new DataOutputStream(socket.getOutputStream()), true);
        open = true;
    }


    //pushCommand method added just to get it to build
    //used in PendingCommandProcessor
    public void pushCommand(ICommand cmd){

    }

    public String readLine() throws IOException {
        if (!open) {
            return null;
        }
        return in.readLine();
    }

    public boolean writeLine(String msg) {
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
