package activitystreamer.server;

import java.net.InetAddress;

public class ServerState {
  private int load;
  private InetAddress hostname;
  private int port;
  private int securePort;

  public ServerState(InetAddress hostname, int port, int load, int securePort) {
    this.hostname = hostname;
    this.port = port;
    this.load = load;
    this.securePort = securePort;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ServerState)) {
      return false;
    }

    ServerState other = (ServerState) o;
    return load == other.load
        && hostname.equals(other.hostname)
        && port == other.port;
  }

  public InetAddress getHostname() {
    return hostname;
  }

  public void setHostname(InetAddress hostname) {
    this.hostname = hostname;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public int getLoad() {
    return load;
  }

  public void setLoad(int load) {
    this.load = load;
  }

  public int getSecurePort() {
    return securePort;
  }

  public void setSecurePort(int securePort) {
    this.securePort = securePort;
  }

  public boolean isSecure() {
    return this.securePort != -1;
  }
}
