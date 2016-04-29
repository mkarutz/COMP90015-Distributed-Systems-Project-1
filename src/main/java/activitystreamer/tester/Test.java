package activitystreamer.tester;

import activitystreamer.core.command.*;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;
import activitystreamer.*;

import com.google.gson.*;

public abstract class Test {
    private Queue<ICommand> requests = new LinkedList<ICommand>();
    private Queue<Expectation> expectations = new LinkedList<Expectation>();
    private Queue<Integer> repeats = new LinkedList<Integer>();
    private List<Process> processes = new LinkedList<Process>();
    private ICommand nextRequest = null;
    private boolean complete = false;
    private int runningCount = -1;
    private boolean lastWasSend;

    TestControl testControl;
    Gson gson;

    public Test(TestControl testControl) {
        this.testControl = testControl;

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ICommand.class, new CommandAdapter())
                .registerTypeAdapter(JsonObject.class, new JsonObjectAdapter())
                .create();

        System.out.println("========== STARTING CONTEXT ==========");
        testContext();
        System.out.println("\n============ RUNNING TEST ============");
        testSpec();
        testSpecPost();
    }

    protected abstract void testContext();
    protected abstract void testSpec();

    protected void send(ICommand cmd) {
        if (runningCount == -1) {
            lastWasSend = true;
            runningCount = 0;
        } else if (!lastWasSend) {
            repeats.add(new Integer(runningCount));
            runningCount = 0;
            lastWasSend = true;
        }
        runningCount++;
        this.requests.add(cmd);
    }

    protected void connect(int port) {
        this.requests.add(new ChangeConnCommand(port));
    }

    protected void expect(Expectation expectation) {
        if (runningCount == -1) {
            lastWasSend = false;
            runningCount = 0;
        } else if (lastWasSend) {
            repeats.add(new Integer(runningCount));
            runningCount = 0;
            lastWasSend = false;
        }
        runningCount++;
        this.expectations.add(expectation);
    }

    protected void server(int localPort) {
        try {
            System.out.println("Starting test server on port " + localPort + "...");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {};
            Process p = Runtime.getRuntime().exec("java -jar build/libs/Server.jar -lp " + localPort + " -d");
            this.processes.add(p);
        } catch (Exception e) {
            System.out.println("Error executing command");
        }
    }

    protected void server(int localPort, int remotePort) {
        try {
            System.out.println("Starting test server on port " + localPort + ", connecting to server on remote port " + remotePort + "...");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {};
            Process p = Runtime.getRuntime().exec("java -jar build/libs/Server.jar -lp " + localPort + " -rh localhost -rp " + remotePort  + " -d");
            this.processes.add(p);
        } catch (Exception e) {
            System.out.println("Error executing command");
        }
    }

    protected void client(int remotePort, String username, String secret) {
        try {
            System.out.println("Starting test client connecting to server on remote port " + remotePort + "...");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {};
            Process p = Runtime.getRuntime().exec("java -jar build/libs/Client.jar -rh localhost -rp " + remotePort  + " -u "+ username +" -s " + secret);
            this.processes.add(p);
        } catch (Exception e) {
            System.out.println("Error executing command");
        }
    }

    protected void testSpecPost() {
        if (runningCount > 0) {
            repeats.add(new Integer(runningCount));
        }
    }

    public void startTest() {
        this.nextRequest = requests.poll();
        System.out.println(this.nextRequest);
    }

    private void request(ICommand cmd) {
        if (cmd instanceof ChangeConnCommand) {
            ChangeConnCommand ccc = (ChangeConnCommand)cmd;
            testControl.changeConn(ccc.port);
        } else {
            System.out.println("REQUEST OUTGOING");
            this.testControl.request(cmd);
        }
    }

    // Note, the code in this method, let alone this class is truly horrible
    // and I'm aware of it. :)
    public void response(ICommand cmd) {
        Integer count = this.repeats.peek();
        if (count == 1) {
            this.repeats.poll();
        } else {
            count--;
        }

        System.out.println("RESPONSE INCOMING");
        Expectation e = this.expectations.poll();
        if (e == null) {
            System.out.println("TEST FAILED - Got a command which wasn't expected!");
            this.complete = true;
            return;
        }
        if (!e.compare(cmd)) {
            System.out.println("TEST FAILED - Expecting different response!");
            this.complete = true;
            return;
        }

        // Continue test, by polling for next request to send
        this.nextRequest = requests.poll();
    }

    public boolean isComplete() {
        return complete;
    }

    public void next() {
        if (nextRequest != null) {
            Integer rpts = this.repeats.poll();
            while (rpts != null && rpts > 0) {
                this.request(nextRequest);
                rpts--;
                if (rpts != 0) {
                    this.nextRequest = requests.poll();
                }
            }
            nextRequest = null;
        } else {
            System.out.println("TEST PASSED!");
            this.complete = true;
        }
    }
}
