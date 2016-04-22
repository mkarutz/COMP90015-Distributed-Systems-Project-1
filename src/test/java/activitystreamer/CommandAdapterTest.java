package activitystreamer;

import activitystreamer.core.command.AuthenticateCommand;
import activitystreamer.core.command.ICommand;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Assert;
import org.junit.Test;

public class CommandAdapterTest {
    @Test
    public void testValidCommandDeserialization() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ICommand.class, new CommandAdapter())
                .create();

        AuthenticateCommand expected = new AuthenticateCommand();
        expected.setSecret("fmnmpp3ai91qb3gc2bvs14g3ue");

        String msg = "{\"command\":\"AUTHENTICATE\",\"secret\":\"fmnmpp3ai91qb3gc2bvs14g3ue\"}";
        ICommand actual = gson.fromJson(msg, ICommand.class);

        Assert.assertEquals(expected, actual);
    }
}
