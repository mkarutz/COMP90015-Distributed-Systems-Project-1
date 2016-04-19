package activitystreamer.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

public class ClientSolution extends Runnable {
    private static final Logger log = LogManager.getLogger();
    private TextFrame textFrame;

    public ClientSolution() {
        // open the gui
        log.debug("opening the gui");
        textFrame = new TextFrame();
    }

    // called by the gui when the user clicks "send"
    public void sendActivityObject(JSONObject activityObj) {

    }

    // called by the gui when the user clicks disconnect
    public void disconnect() {
        textFrame.setVisible(false);
    }


    // the client's run method, to receive messages
    @Override
    public void run() {
		
    }
}
