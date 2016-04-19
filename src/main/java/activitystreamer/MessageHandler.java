package activitystreamer;

import activitystreamer.server.Connection;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Map;

public class MessageHandler {
    Map<String, Class> commandClassMap;
    private JsonParser jsonParser;
    private Gson gson;

    public MessageHandler(JsonParser jsonParser, Gson gson) {
        this.jsonParser = jsonParser;
        this.gson = gson;
    }

    public void handleMessage(Connection connection, String msg) {
        JsonObject json = jsonParser.parse(msg).getAsJsonObject();
        if (!json.has("command")) {

        }
        Class c = commandClassMap.get(json.get("command").getAsString());
        Object command = gson.fromJson(json, c);

        // ...
    }
}
