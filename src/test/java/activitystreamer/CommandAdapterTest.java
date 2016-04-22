package activitystreamer;

import activitystreamer.core.command.AuthenticateCommand;
import activitystreamer.core.command.ICommand;
import com.google.gson.*;
import org.junit.Assert;
import org.junit.Test;
import java.lang.reflect.Type;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class CommandAdapterTest {
    @Test
    public void testValidCommandDeserialization() {
        Class<ICommand> type = ICommand.class;

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(type, new CommandAdapter())
                .create();

        AuthenticateCommand expected = new AuthenticateCommand();
        expected.setSecret("fmnmpp3ai91qb3gc2bvs14g3ue");

        String msg = "{\"command\":\"AUTHENTICATE\",\"secret\":\"fmnmpp3ai91qb3gc2bvs14g3ue\"}";
        JsonObject elem = new JsonParser().parse(msg).getAsJsonObject();

        ICommand actual = gson.fromJson(elem, type);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testAdaptorIsCalled() {
        Class<ICommand> type = ICommand.class;
        CommandAdapter adapter = spy(new CommandAdapter());

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(type, adapter)
                .create();

        String msg = "{\"command\":\"AUTHENTICATE\",\"secret\":\"fmnmpp3ai91qb3gc2bvs14g3ue\"}";
        JsonObject elem = new JsonParser().parse(msg).getAsJsonObject();
        gson.fromJson(elem, type);

        verify(adapter).deserialize(any(JsonElement.class), any(Type.class), any(JsonDeserializationContext.class));
    }

    @Test
    public void testSerializeDeserializePreservesEquality() {
        Class<ICommand> type = ICommand.class;
        CommandAdapter adapter = new CommandAdapter();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(type, adapter)
                .create();

        AuthenticateCommand cmd = new AuthenticateCommand();
        cmd.setSecret("foo bar");

        String json = gson.toJson(cmd, type);
        ICommand cmd2 = gson.fromJson(json, type);

        assertEquals(cmd, cmd2);
    }
}
