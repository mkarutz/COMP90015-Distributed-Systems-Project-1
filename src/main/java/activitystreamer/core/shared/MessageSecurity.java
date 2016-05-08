package activitystreamer.core.shared;

import javax.crypto.*;
import java.security.*;

public class MessageSecurity{

    private static final int keySize=1024;

    public KeyPair getKeys(){
        try{
            KeyPairGenerator keyGenerator=KeyPairGenerator.getInstance("RSA");
            keyGenerator.initialize(keySize);
            KeyPair keys = keyGenerator.generateKeyPair();
            return keys;
        }catch(NoSuchAlgorithmException e){
            System.out.println(e);
        }
        return null;
    }

}
