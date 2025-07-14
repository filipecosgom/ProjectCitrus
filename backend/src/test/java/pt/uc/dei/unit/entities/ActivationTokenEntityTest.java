package pt.uc.dei.unit.entities;

import pt.uc.dei.entities.ActivationTokenEntity;
import pt.uc.dei.entities.TemporaryUserEntity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ActivationTokenEntity}.
 */
class ActivationTokenEntityTest {
    @Test
    void testGettersAndSetters() {
        ActivationTokenEntity entity = new ActivationTokenEntity();
        Long id = 1L;
        String tokenValue = "token123";
        LocalDateTime creationDate = LocalDateTime.now();
        TemporaryUserEntity tempUser = new TemporaryUserEntity();

        entity.setId(id);
        entity.setTokenValue(tokenValue);
        entity.setCreationDate(creationDate);
        entity.setTemporaryUser(tempUser);

        assertEquals(id, entity.getId());
        assertEquals(tokenValue, entity.getTokenValue());
        assertEquals(creationDate, entity.getCreationDate());
        assertEquals(tempUser, entity.getTemporaryUser());
    }
}
