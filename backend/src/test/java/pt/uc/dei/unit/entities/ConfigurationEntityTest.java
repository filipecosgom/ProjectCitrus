package pt.uc.dei.entities;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ConfigurationEntity}.
 */
class ConfigurationEntityTest {
    @Test
    void testGettersAndSetters() {
        ConfigurationEntity entity = new ConfigurationEntity();
        Long id = 1L;
        Integer loginTime = 10;
        Integer verificationTime = 20;
        Integer passwordResetTime = 30;
        LocalDateTime creationDate = LocalDateTime.now();
        UserEntity admin = new UserEntity();

        entity.setId(id);
        entity.setLoginTime(loginTime);
        entity.setVerificationTime(verificationTime);
        entity.setPasswordResetTime(passwordResetTime);
        entity.setCreationDate(creationDate);
        entity.setAdmin(admin);

        assertEquals(id, entity.getId());
        assertEquals(loginTime, entity.getLoginTime());
        assertEquals(verificationTime, entity.getVerificationTime());
        assertEquals(passwordResetTime, entity.getPasswordResetTime());
        assertEquals(creationDate, entity.getCreationDate());
        assertEquals(admin, entity.getAdmin());
    }
}
