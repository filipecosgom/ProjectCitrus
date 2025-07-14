package pt.uc.dei.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilsTest {
    @Test
    void testEncryptAndVerify_Success() {
        String password = "MySecret123!";
        String hash = PasswordUtils.encrypt(password);
        assertNotNull(hash);
        assertTrue(hash.startsWith("$2a$") || hash.startsWith("$2b$") || hash.startsWith("$2y$"));
        assertTrue(PasswordUtils.verify(hash, password));
    }

    @Test
    void testVerify_Failure() {
        String password = "MySecret123!";
        String wrongPassword = "WrongPassword";
        String hash = PasswordUtils.encrypt(password);
        assertFalse(PasswordUtils.verify(hash, wrongPassword));
    }

    @Test
    void testEncrypt_DifferentHashesForSamePassword() {
        String password = "RepeatMe";
        String hash1 = PasswordUtils.encrypt(password);
        String hash2 = PasswordUtils.encrypt(password);
        assertNotEquals(hash1, hash2, "Hashes should be different due to random salt");
        assertTrue(PasswordUtils.verify(hash1, password));
        assertTrue(PasswordUtils.verify(hash2, password));
    }

    @Test
    void testVerify_InvalidHash() {
        String password = "test";
        String invalidHash = "$2a$10$invalidhashstringnotvalidatall";
        assertFalse(PasswordUtils.verify(invalidHash, password));
    }
}
