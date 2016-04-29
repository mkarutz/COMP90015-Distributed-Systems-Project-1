package activitystreamer.server.container;

import activitystreamer.server.services.contracts.BroadcastService;
import activitystreamer.server.services.impl.NetworkManagerService;
import com.google.inject.AbstractModule;

public class Container extends AbstractModule {
    @Override
    protected void configure() {
        bind(BroadcastService.class).to(NetworkManagerService.class);
    }
}
