package pt.uc.dei.utils;

package com.yourcompany.security;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

public class TwoFactorUtil {

    // Reusable GoogleAuthenticator instance.
    private static final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    /**
     * Generates a new TOTP secret key for 2FA.
     *
     * @return a GoogleAuthenticatorKey containing the secret key.
     */
    public static GoogleAuthenticatorKey generateSecretKey() {
        return gAuth.createCredentials();
    }

    /**
     * Extracts the Base32 secret string from the provided GoogleAuthenticatorKey.
     *
     * @param key the GoogleAuthenticatorKey obtained from generateSecretKey()
     * @return the Base32-encoded secret string.
     */
    public static String getSecretKeyString(GoogleAuthenticatorKey key) {
        return key.getKey();
    }
}