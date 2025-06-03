package pt.uc.dei.utils;

import java.security.SecureRandom;
import java.util.Base64;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for generating a random 256-bit encryption key.
 * <p>
 * - Uses `SecureRandom` to create a cryptographically strong random key.
 * - Encodes the key in Base64 format for easy storage and usage.
 * - Prints the generated key to the console.
 * </p>
 */
public class Keygen {

    /**
     * Main method to generate and display a random 256-bit key.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        // Create a byte array of length 32 (256 bits)
        byte[] key = new byte[32];

        // Use SecureRandom to fill the array with random values
        new SecureRandom().nextBytes(key);

        // Encode the key as a Base64 string for easy storage and retrieval
        String encodedKey = Base64.getEncoder().encodeToString(key);

        // Print the generated key to the console
        System.out.println(encodedKey);
    }
}