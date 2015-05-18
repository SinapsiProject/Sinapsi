package com.sinapsi.webservice.security;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import org.apache.commons.codec.binary.Base64;

/**
 * This class give a secure way to store passwords in the db using hash function
 *
 */
public class Password {
    // a higher number of iteration make slow the calculation of the hash, but
    // make a stronger Encryption
    private static final int iterations = 20 * 100;
    private static final int saltLen = 32;
    private static final int desiredKeyLen = 256;

    /**
     * Computes a salted PBKDF2 hash of given plaintext password (Empty
     * passwords are not supported)
     * 
     * @return return the Encrypted password
     * @param password password to encrypt
     */
    public static String getSaltedHash(String password) throws Exception {
        byte[] salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLen);
        // store the salt with the password
        return Base64.encodeBase64String(salt) + "$" + hash(password, salt);
    }

    /**
     * Checks whether given plaintext password corresponds to a stored salted
     * hash of the password.
     * 
     * @return return true if passwords matches, false otherwise
     * @param password the password to check
     * @param stored the password stored in the db
     * @throw Exception
     * */
    public static boolean check(String password, String stored) throws Exception {
        String[] saltAndPass = stored.split("\\$");
        if (saltAndPass.length != 2) {
            throw new IllegalStateException("The stored password have the form 'salt$hash'");
        }
        
        String hashOfInput = hash(password, Base64.decodeBase64(saltAndPass[0]));
        return hashOfInput.equals(saltAndPass[1]);
    }

    /**
     * Calculate the hash of the given password
     * 
     * @param password
     * @param salt
     * @return hash of the password
     * @throws Exception
     */
    private static String hash(String password, byte[] salt) throws Exception {
        if (password == null || password.length() == 0)
            throw new IllegalArgumentException( "Empty passwords are not supported.");
        
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey key = f.generateSecret(new PBEKeySpec(password.toCharArray(), salt, iterations, desiredKeyLen));
        return Base64.encodeBase64String(key.getEncoded());
    }
}