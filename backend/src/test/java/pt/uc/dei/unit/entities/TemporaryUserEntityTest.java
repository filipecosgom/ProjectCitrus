package pt.uc.dei.unit.entities;

import pt.uc.dei.entities.TemporaryUserEntity;
import pt.uc.dei.entities.ActivationTokenEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TemporaryUserEntityTest {
    private TemporaryUserEntity tempUser;
    private ActivationTokenEntity activationToken;

    @BeforeEach
    void setUp() {
        tempUser = new TemporaryUserEntity();
        activationToken = new ActivationTokenEntity();
        tempUser.setId(1L);
        tempUser.setEmail("test@example.com");
        tempUser.setPassword("hashedPassword");
        tempUser.setSecretKey("secretKey123");
        tempUser.setActivationToken(activationToken);
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1L, tempUser.getId());
        assertEquals("test@example.com", tempUser.getEmail());
        assertEquals("hashedPassword", tempUser.getPassword());
        assertEquals("secretKey123", tempUser.getSecretKey());
        assertEquals(activationToken, tempUser.getActivationToken());
    }

    @Test
    void testSettersUpdateValues() {
        tempUser.setEmail("new@example.com");
        assertEquals("new@example.com", tempUser.getEmail());
        tempUser.setPassword("newPassword");
        assertEquals("newPassword", tempUser.getPassword());
        tempUser.setSecretKey("newSecret");
        assertEquals("newSecret", tempUser.getSecretKey());
        ActivationTokenEntity newToken = new ActivationTokenEntity();
        tempUser.setActivationToken(newToken);
        assertEquals(newToken, tempUser.getActivationToken());
    }
}
