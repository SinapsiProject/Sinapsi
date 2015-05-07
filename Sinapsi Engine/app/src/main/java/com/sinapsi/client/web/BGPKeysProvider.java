package com.sinapsi.client.web;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;



/**
 * Interface used by BGPGsonConverter to get public and private keys
 * to crypt/encrypt data.
 */
public interface BGPKeysProvider {
    /**
     * Public key getter
     * @return the public key
     */
    public PublicKey getPublicKey();

    /**
     * Private key getter
     * @return the private key
     */
    public PrivateKey getPrivateKey();

    /**
     * Key pair getter
     * @return the key pair
     */
    public KeyPair getKeyPair();
}
