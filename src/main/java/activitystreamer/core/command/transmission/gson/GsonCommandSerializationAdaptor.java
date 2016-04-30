package activitystreamer.core.command.transmission.gson;

import activitystreamer.core.command.*;
import activitystreamer.core.command.transmission.CommandDeserializer;
import activitystreamer.core.command.transmission.CommandParseException;
import activitystreamer.core.command.transmission.CommandSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class GsonCommandSerializationAdaptor implements CommandDeserializer, CommandSerializer {
    private Gson gson;

    public GsonCommandSerializationAdaptor() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Command.class, new CommandAdapter())
                .registerTypeAdapter(JsonObject.class, new JsonObjectAdapter())
                .registerTypeAdapter(AuthenticateCommand.class, new AnnotatedDeserializer())
                .registerTypeAdapter(InvalidMessageCommand.class, new AnnotatedDeserializer())
                .registerTypeAdapter(AuthenticationFailCommand.class, new AnnotatedDeserializer())
                .registerTypeAdapter(LoginCommand.class, new AnnotatedDeserializer())
                .registerTypeAdapter(LoginSuccessCommand.class, new AnnotatedDeserializer())
                .registerTypeAdapter(RedirectCommand.class, new AnnotatedDeserializer())
                .registerTypeAdapter(LoginFailedCommand.class, new AnnotatedDeserializer())
                .registerTypeAdapter(LogoutCommand.class, new AnnotatedDeserializer())
                .registerTypeAdapter(ActivityMessageCommand.class, new AnnotatedDeserializer())
                .registerTypeAdapter(ServerAnnounceCommand.class, new AnnotatedDeserializer())
                .registerTypeAdapter(ActivityBroadcastCommand.class, new AnnotatedDeserializer())
                .registerTypeAdapter(RegisterCommand.class, new AnnotatedDeserializer())
                .registerTypeAdapter(RegisterFailedCommand.class, new AnnotatedDeserializer())
                .registerTypeAdapter(ActivityMessageCommand.class, new AnnotatedDeserializer())
                .registerTypeAdapter(RegisterSuccessCommand.class, new AnnotatedDeserializer())
                .registerTypeAdapter(LockRequestCommand.class, new AnnotatedDeserializer())
                .registerTypeAdapter(LockDeniedCommand.class, new AnnotatedDeserializer())
                .registerTypeAdapter(LockAllowedCommand.class, new AnnotatedDeserializer())
                .create();
    }

    @Override
    public Command deserialize(String message) throws CommandParseException {
        try {
            return gson.fromJson(message, Command.class);
        } catch (JsonParseException e) {
            throw new CommandParseException(e.getMessage());
        }
    }

    @Override
    public String serialize(Command command) {
        return gson.toJson(command, Command.class);
    }
}
