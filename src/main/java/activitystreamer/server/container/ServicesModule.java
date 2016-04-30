package activitystreamer.server.container;

import activitystreamer.core.command.transmission.CommandDeserializer;
import activitystreamer.core.command.transmission.CommandSerializer;
import activitystreamer.core.command.transmission.gson.GsonCommandSerializationAdaptor;
import activitystreamer.core.commandprocessor.CommandProcessor;
import activitystreamer.server.Control;
import activitystreamer.server.IncomingConnectionHandler;
import activitystreamer.server.commandprocessors.MainCommandProcessor;
import activitystreamer.server.services.contracts.*;
import activitystreamer.server.services.contracts.RemoteServerStateService;
import activitystreamer.server.services.contracts.ServerAuthService;
import activitystreamer.server.services.contracts.UserAuthService;
import activitystreamer.server.services.impl.*;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class ServicesModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(BroadcastService.class).to(NetworkManagerService.class).in(Singleton.class);
        bind(ConnectionManager.class).to(NetworkManagerService.class).in(Singleton.class);
        bind(RemoteServerStateService.class).to(ConcreteRemoteServerStateService.class).in(Singleton.class);
        bind(ServerAuthService.class).to(ConcreteServerAuthService.class).in(Singleton.class);
        bind(UserAuthService.class).to(ConcreteUserAuthService.class).in(Singleton.class);
        bind(IncomingConnectionHandler.class).to(Control.class).in(Singleton.class);
        bind(CommandDeserializer.class).to(GsonCommandSerializationAdaptor.class).in(Singleton.class);
        bind(CommandSerializer.class).to(GsonCommandSerializationAdaptor.class).in(Singleton.class);
        bind(GsonCommandSerializationAdaptor.class).in(Singleton.class);
        bind(CommandProcessor.class).to(MainCommandProcessor.class).in(Singleton.class);
    }
}
