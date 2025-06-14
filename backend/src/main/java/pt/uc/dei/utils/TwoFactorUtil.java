package pt.uc.dei.utils;

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

    /**
     * Verifies the provided TOTP code against the stored secret key.
     *
     * @param secretKey The user's stored secret key (Base32 encoded).
     * @param userCode The code entered by the user.
     * @return true if the code is valid, false otherwise.
     */
    public static boolean verifyTwoFactorCode(String secretKey, int userCode) {
        return gAuth.authorize(secretKey, userCode);
    }

}