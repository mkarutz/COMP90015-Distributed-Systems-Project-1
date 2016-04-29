package activitystreamer.tester;

import activitystreamer.core.command.*;

import com.google.gson.*;

public class TestLoginAnon extends Test {
    public TestLoginAnon(TestControl testControl) {
        super(testControl);
    }

    @Override
    protected void testContext() {
        server(4002);
        server(4005, 4002);
        client(4002, "anonymous", "");
    }

    @Override
    protected void testSpec() {
        send(new LoginCommand("anonymous", null));
        expect(new ExpectCommandType(LoginSuccessCommand.class));
        expect(new ExpectCommandType(RedirectCommand.class));

        String jsonActivity = "{\"foo\":\"bar\"}"; // Probably best to add an actual activity obj here
        JsonObject jsonActivityObj = new JsonParser().parse(jsonActivity).getAsJsonObject();

        send(new ActivityMessageCommand("anonymous", "", jsonActivityObj));
        send(new ActivityMessageCommand("anonymous", "", jsonActivityObj));
        send(new ActivityMessageCommand("anonymous", "", jsonActivityObj));
        send(new ActivityMessageCommand("anonymous", "", jsonActivityObj));
    }
}
