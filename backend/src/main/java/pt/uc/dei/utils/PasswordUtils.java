package pt.uc.dei.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;
import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Utility class for password encryption and verification using the BCrypt algorithm.
 * <p>
 * - Ensures secure password storage by hashing passwords with a salt.
 * - Allows verification of a plain-text password against its hashed version.
 * - Uses a default hashing strength of 10 rounds.
 * </p>
 */
public class PasswordUtils {

    /**
     * Verifies a password against its stored, hashed version.
     *
     * @param hashedPassword The hashed password stored in the database.
     * @param plainPassword  The plain-text password provided by the user.
     * @return {@code true} if the password matches the hashed version, {@code false} otherwise.
     */
    public static boolean verify(String hashedPassword, String plainPassword) {
        // Use BCrypt's built-in verification method to compare hashed password and input password.
        BCrypt.Result result = BCrypt.verifyer().verify(plainPassword.toCharArray(), hashedPassword.toCharArray());

        // Returns true if the password matches, false otherwise.
        return result.verified;
    }

    /**
     * Encrypts a given password using the BCrypt algorithm with a predefined hashing strength.
     *
     * @param password The plain-text password to be encrypted.
     * @return The securely hashed password string.
     */
    public static String encrypt(String password) {
        // Hash password using BCrypt with a cost factor of 10 (higher values increase security).
        return BCrypt.withDefaults().hashToString(10, password.toCharArray());
    }
}