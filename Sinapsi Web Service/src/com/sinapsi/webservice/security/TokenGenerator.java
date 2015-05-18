package com.sinapsi.webservice.security;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Generate alphanumeric secure random token
 *
 */
public final class TokenGenerator {
    private SecureRandom random = new SecureRandom();
    private int bits;
    
    /**
     * Default ctor
     */
    public TokenGenerator(int bits) {
        this.bits = bits;
    }
    
    /**
     * Non static method to return the generated token
     * @return
     */
    public String generateAllOne() {
        return new BigInteger(bits, random).toString(32);
    }
    
    /**
     * Static method to return alphanumeric secure random token
     * @return
     */
    public static String generate() {
        return new BigInteger(128, new SecureRandom()).toString(32);
    }
}
