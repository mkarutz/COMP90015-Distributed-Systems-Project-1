package activitystreamer.core.command.transmission.gson;

import com.google.gson.*;

import java.lang.reflect.Type;

public class JsonObjectAdapter implements JsonSerializer<JsonObject>, JsonDeserializer<JsonObject> {
  @Override
  public JsonObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return json.getAsJsonObject();
  }

  @Override
  public JsonElement serialize(JsonObject src, Type typeOfSrc, JsonSerializationContext context) {
    return src;
  }
}
