package activitystreamer.tester;

import activitystreamer.core.command.transmission.gson.GsonCommandSerializationAdaptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import activitystreamer.core.command.*;
import activitystreamer.core.shared.Connection;

import activitystreamer.util.Settings;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class TestControl implements Runnable {
    private static final Logger log = LogManager.getLogger();
    private boolean term = false;
    private Queue<Test> tests = new LinkedList<Test>();
    private Test currentTest = null;

    private String hostname;
    private int port;
    private Connection connection;

    public TestControl(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void response(ICommand cmd) {
        currentTest.response(cmd);
    }

    public void request(ICommand cmd) {
        connection.pushCommand(cmd);
    }

    public void addTest(Test test) {
        tests.add(test);
    }

    @Override
    public void run() {
        initiateConnection();
        currentTest = tests.poll();
        currentTest.startTest();
        while (!term && currentTest != null) {

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                log.info("received an interrupt, system is shutting down");
                break;
            }

            if (!currentTest.isComplete()) {
                currentTest.next();
            } else {
                currentTest = tests.poll();
                if (currentTest != null) {
                    currentTest.startTest();
                }
            }
        }

        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                log.info("received an interrupt, system is shutting down");
                break;
            }
        }

        System.out.println("TEST COMPLETE");
    }

    public void initiateConnection() {
        if (Settings.getRemoteHostname() != null) {
            try {
                outgoingConnection(new Socket(this.hostname, this.port));
            } catch (IOException e) {
                log.error("failed to make connection :" + e);
                System.exit(-1);
            }
        }
    }

    public void changeConn(int port) {
        connection.close();
        this.port = port;
        initiateConnection();
    }

    public synchronized Connection outgoingConnection(Socket s) throws IOException {
        log.debug("outgoing connection: " + Settings.socketAddress(s));
        connection = new Connection(s,
                new GsonCommandSerializationAdaptor(),
                new GsonCommandSerializationAdaptor(),
                new TestCommandProcessor(this)
        );
        new Thread(connection).start();
        return connection;
    }
}
