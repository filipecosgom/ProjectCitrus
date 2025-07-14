package pt.uc.dei.unit.dtos;

import org.junit.jupiter.api.Test;
import pt.uc.dei.dtos.ConfigurationDTO;
import pt.uc.dei.dtos.UserDTO;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link ConfigurationDTO}.
 */
class ConfigurationDTOTest {
    @Test
    void testGettersAndSetters() {
        ConfigurationDTO dto = new ConfigurationDTO();
        Long id = 1L;
        Integer loginTime = 10;
        Integer verificationTime = 20;
        Integer passwordResetTime = 30;
        LocalDateTime creationDate = LocalDateTime.now();
        Long adminId = 2L;
        UserDTO admin = null;

        dto.setId(id);
        dto.setLoginTime(loginTime);
        dto.setVerificationTime(verificationTime);
        dto.setPasswordResetTime(passwordResetTime);
        dto.setCreationDate(creationDate);
        dto.setAdminId(adminId);
        dto.setAdmin(admin);

        assertEquals(id, dto.getId());
        assertEquals(loginTime, dto.getLoginTime());
        assertEquals(verificationTime, dto.getVerificationTime());
        assertEquals(passwordResetTime, dto.getPasswordResetTime());
        assertEquals(creationDate, dto.getCreationDate());
        assertEquals(adminId, dto.getAdminId());
        assertEquals(admin, dto.getAdmin());
    }
}
