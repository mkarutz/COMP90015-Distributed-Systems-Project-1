package activitystreamer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import org.junit.Test;

public class AnnotatedDeserializerTest {
    @Test(expected=JsonParseException.class)
    public void testIfAnAnnotatedFieldIsMissingAJsonParseExceptionIsThrown() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(AnnotatedPOJO.class, new AnnotatedDeserializer<AnnotatedPOJO>())
                .create();

        String json = "{\"optionalField\":10}";
        gson.fromJson(json, AnnotatedPOJO.class);
    }

    @Test
    public void testIfAnNonAnnotatedFieldIsMissingThenExpectNothing() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(AnnotatedPOJO.class, new AnnotatedDeserializer<AnnotatedPOJO>())
                .create();

        String json = "{\"requiredField\":\"foo\"}";
        gson.fromJson(json, AnnotatedPOJO.class);
    }

    @Test(expected=JsonParseException.class)
    public void testRequiredPrimitiveType() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(AnnotatedPOJO2.class, new AnnotatedDeserializer<AnnotatedPOJO2>())
                .create();

        String json = "{\"optionalField\":10}";
        gson.fromJson(json, AnnotatedPOJO2.class);
    }

    class AnnotatedPOJO {
        @JsonRequired
        public String requiredField;
        public int optionalField;
    }

    class AnnotatedPOJO2 {
        @JsonRequired
        public int requiredField;
        public int optionalField;
    }
}
