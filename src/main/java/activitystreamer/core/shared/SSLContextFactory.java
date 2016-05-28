package activitystreamer.core.shared;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.*;
import java.io.InputStream;
import java.security.KeyStore;

public class SSLContextFactory {
  private Logger log = LogManager.getLogger();
  private SSLContext sslCtx;

  private String privateKeys;
  private String privateKeyPass;
  private String publicKeys;
  private String publicKeyPass;

  public SSLContextFactory(String privateKeys, String privateKeyPass, String publicKeys, String publicKeyPass) {
    this.privateKeys = privateKeys;
    this.publicKeys = publicKeys;
    this.privateKeyPass = privateKeyPass;
    this.publicKeyPass = publicKeyPass;
  }

  public SSLContext getContext() {
    try {
      KeyManager[] keyManagers = null;
      TrustManager[] trustManagers = null;

      if (privateKeys != null) {
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        KeyStore privKeystore = KeyStore.getInstance(KeyStore.getDefaultType());
        InputStream keystoreStream = getClass().getResourceAsStream(privateKeys);
        privKeystore.load(keystoreStream, privateKeyPass.toCharArray());

        keyManagerFactory.init(privKeystore, privateKeyPass.toCharArray());
        keyManagers = keyManagerFactory.getKeyManagers();
      }

      if (publicKeys != null) {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        InputStream keystoreStreamP = getClass().getResourceAsStream(publicKeys); // note, not getSYSTEMResourceAsStream
        keystore.load(keystoreStreamP, publicKeyPass.toCharArray());

        trustManagerFactory.init(keystore);
        trustManagers = trustManagerFactory.getTrustManagers();
      }

      sslCtx = SSLContext.getInstance("TLS");
      sslCtx.init(keyManagers, trustManagers, null);
      return sslCtx;

    } catch (Exception e) {
      log.error(e.getMessage());
      System.exit(-1);
      return null;
    }
  }
}
