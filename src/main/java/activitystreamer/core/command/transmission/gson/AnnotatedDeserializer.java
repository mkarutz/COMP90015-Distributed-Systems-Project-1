package activitystreamer.core.command.transmission.gson;

import com.google.gson.*;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class AnnotatedDeserializer<T> implements JsonDeserializer<T> {
    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) {
            throw new JsonParseException("No JSON Object found.");
        }

        T pojo = new Gson().fromJson(json, typeOfT);
        JsonObject jsonObject = json.getAsJsonObject();

        Field[] fields = pojo.getClass().getDeclaredFields();
        for (Field f: fields) {
            if (f.getAnnotation(JsonRequired.class) != null) {
                if (!jsonObject.has(f.getName())) {
                    throw new JsonParseException("Missing field in JSON: " + f.getName());
                }
            }
        }
        return pojo;
    }
}
