package activitystreamer.core.command.transmission.gson;

import activitystreamer.core.command.Command;
import activitystreamer.core.command.LoginCommand;
import activitystreamer.core.command.transmission.CommandParseException;
import org.junit.Assert;
import org.junit.Test;

public class GsonCommandSerializationAdaptorTest {
    @Test
    public void testValidDeserialization() throws CommandParseException {
        GsonCommandSerializationAdaptor deserializer = new GsonCommandSerializationAdaptor();
        String message = "{\n" +
                "    \"command\" : \"LOGIN\",\n" +
                "    \"username\" : \"aaron\",\n" +
                "    \"secret\" : \"fmnmpp3ai91qb3gc2bvs14g3ue\"\n" +
                "}";
        Command command = deserializer.deserialize(message);
        LoginCommand expected = new LoginCommand("aaron", "fmnmpp3ai91qb3gc2bvs14g3ue");
        Assert.assertEquals(command, expected);
    }

    @Test(expected=CommandParseException.class)
    public void testInalidDeserialization() throws CommandParseException {
        GsonCommandSerializationAdaptor deserializer = new GsonCommandSerializationAdaptor();
        String message = "{\n" +
                "    \"command\" : \"LOGIN\",\n" +
                "    \"secret\" : \"fmnmpp3ai91qb3gc2bvs14g3ue\"\n" +
                "}";
        deserializer.deserialize(message);
    }
}
