package activitystreamer.server.services.contracts;

import java.util.Set;

public interface IRemoteServerStateService {
    Set<String> getKnownServerIds();

}
