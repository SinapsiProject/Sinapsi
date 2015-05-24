package com.sinapsi.client.web;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.SecretKey;


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
     * Server public key getter
     * @return
     */
    public PublicKey getServerPublicKey();

    /**
     * Private key getter
     * @return the private key
     */
    public PrivateKey getPrivateKey();

    /**
     * Secret Key getter
     * @return the session key
     */
    public SecretKey getServerSessionKey();

    /**
     * Local Secret Key getter
     * @return secret key
     */
    public SecretKey getLocalUncryptedSessionKey();
}
