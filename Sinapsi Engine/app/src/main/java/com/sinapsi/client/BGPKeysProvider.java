package com.sinapsi.client;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;



/**
 * Interface used by BGPGsonConverter to get public and private keys
 * to crypt/encrypt data.
 */
public interface BGPKeysProvider {
    public PublicKey getPublicKey();
    public PrivateKey getPrivateKey();
    public KeyPair getKeyPair();
}
