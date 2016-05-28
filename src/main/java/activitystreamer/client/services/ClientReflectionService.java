package activitystreamer.client.services;

import com.google.gson.JsonObject;

import java.util.LinkedList;
import java.util.Queue;

public class ClientReflectionService {
  Queue<JsonObject> jActivityQueue;

  public ClientReflectionService() {
    this.jActivityQueue = new LinkedList<JsonObject>();
  }

  public void pushActivityJSON(JsonObject activity) {
    jActivityQueue.add(activity);
  }

  // Returns null if no more activity objects to pop
  public JsonObject popActivityJSON() {
    return jActivityQueue.poll();
  }
}
