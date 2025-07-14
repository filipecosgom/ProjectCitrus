package pt.uc.dei.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PasswordResetTokenEntityTest {
    private PasswordResetTokenEntity token;
    private UserEntity user;

    @BeforeEach
    void setUp() {
        token = new PasswordResetTokenEntity();
        user = new UserEntity();
        token.setId(1L);
        token.setTokenValue("reset-token-123");
        token.setCreationDate(LocalDateTime.of(2025, 7, 14, 12, 0));
        token.setUser(user);
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1L, token.getId());
        assertEquals("reset-token-123", token.getTokenValue());
        assertEquals(LocalDateTime.of(2025, 7, 14, 12, 0), token.getCreationDate());
        assertEquals(user, token.getUser());
    }

    @Test
    void testSettersUpdateValues() {
        token.setTokenValue("new-token");
        assertEquals("new-token", token.getTokenValue());
        token.setCreationDate(LocalDateTime.of(2026, 1, 1, 10, 0));
        assertEquals(LocalDateTime.of(2026, 1, 1, 10, 0), token.getCreationDate());
        UserEntity newUser = new UserEntity();
        token.setUser(newUser);
        assertEquals(newUser, token.getUser());
    }
}
