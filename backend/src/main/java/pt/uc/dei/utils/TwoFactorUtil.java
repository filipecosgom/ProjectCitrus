package pt.uc.dei.utils;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

/**
 * Utility class for Two-Factor Authentication (2FA) operations using TOTP (Time-based One-Time Passwords).
 * <p>
 * Provides methods to generate secret keys, extract secret strings, verify TOTP codes, and validate code format.
 * Uses the GoogleAuthenticator library for TOTP generation and validation.
 */
public class TwoFactorUtil {

    /**
     * Reusable GoogleAuthenticator instance for TOTP operations.
     */
    private static final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    /**
     * Generates a new TOTP secret key for 2FA.
     *
     * @return a {@link GoogleAuthenticatorKey} containing the generated secret key and related data
     */
    public static GoogleAuthenticatorKey generateSecretKey() {
        return gAuth.createCredentials();
    }

    /**
     * Extracts the Base32-encoded secret string from the provided {@link GoogleAuthenticatorKey}.
     *
     * @param key the {@link GoogleAuthenticatorKey} obtained from {@link #generateSecretKey()}
     * @return the Base32-encoded secret string
     */
    public static String getSecretKeyString(GoogleAuthenticatorKey key) {
        return key.getKey();
    }

    /**
     * Verifies the provided TOTP code against the stored secret key.
     * <p>
     * The code is parsed as an integer and checked using the GoogleAuthenticator library.
     *
     * @param secretKey the user's stored secret key (Base32 encoded)
     * @param userCode the code entered by the user
     * @return {@code true} if the code is valid, {@code false} otherwise
     * @throws NumberFormatException if {@code userCode} is null, empty, or not a valid integer
     */
    public static boolean verifyTwoFactorCode(String secretKey, String userCode) {
        if (userCode == null || userCode.isEmpty()) {}
        return gAuth.authorize(secretKey, Integer.parseInt(userCode));
    }

    /**
     * Validates if the provided code is a 6-digit string (after trimming).
     *
     * @param userCode the code entered by the user
     * @return {@code true} if the code is exactly 6 digits after trimming, {@code false} otherwise
     */
    public static boolean validateCode(String userCode) {
        String trimmed = userCode.trim();
        return trimmed.matches("\\d{6}");
    }

}