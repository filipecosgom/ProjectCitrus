package pt.uc.dei.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.uc.dei.dtos.ConfigurationDTO;
import pt.uc.dei.entities.ConfigurationEntity;
import pt.uc.dei.entities.UserEntity;
import pt.uc.dei.repositories.ConfigurationRepository;
import pt.uc.dei.repositories.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfigurationServiceTest {
    @Mock ConfigurationRepository configurationRepository;
    @Mock UserService userService;
    @Mock UserRepository userRepository;
    @InjectMocks ConfigurationService configurationService;

    private ConfigurationEntity configurationEntity;
    private ConfigurationDTO configurationDTO;
    private UserEntity adminUser;

    @BeforeEach
    void setUp() {
        configurationEntity = new ConfigurationEntity();
        configurationEntity.setId(1L);
        configurationEntity.setCreationDate(LocalDateTime.now());
        configurationEntity.setPasswordResetTime(30);
        configurationEntity.setLoginTime(15);
        configurationEntity.setVerificationTime(10);
        configurationEntity.setTwoFactorAuthEnabled(true);
        adminUser = new UserEntity();
        adminUser.setId(2L);
        configurationEntity.setAdmin(adminUser);

        configurationDTO = new ConfigurationDTO();
        configurationDTO.setId(1L);
        configurationDTO.setAdminId(2L);
        configurationDTO.setPasswordResetTime(30);
        configurationDTO.setLoginTime(15);
        configurationDTO.setVerificationTime(10);
        configurationDTO.setTwoFactorAuthEnabled(true);
    }

    @Nested
    @DisplayName("getLatestConfiguration")
    class GetLatestConfiguration {
        @Test
        void returnsConfigurationDTOIfFound() {
            when(configurationRepository.getLatestConfiguration()).thenReturn(configurationEntity);
            ConfigurationDTO result = configurationService.getLatestConfiguration();
            assertNotNull(result);
            assertEquals(configurationEntity.getId(), result.getId());
            assertEquals(configurationEntity.getAdmin().getId(), result.getAdminId());
            assertEquals(configurationEntity.getPasswordResetTime(), result.getPasswordResetTime());
            assertEquals(configurationEntity.getLoginTime(), result.getLoginTime());
            assertEquals(configurationEntity.getVerificationTime(), result.getVerificationTime());
            assertEquals(configurationEntity.getTwoFactorAuthEnabled(), result.getTwoFactorAuthEnabled());
        }
        @Test
        void returnsNullOnException() {
            when(configurationRepository.getLatestConfiguration()).thenThrow(new RuntimeException());
            ConfigurationDTO result = configurationService.getLatestConfiguration();
            assertNull(result);
        }
    }

    @Nested
    @DisplayName("createNewConfiguration")
    class CreateNewConfiguration {
        @Test
        void returnsTrueOnSuccess() {
            when(userRepository.findUserById(2L)).thenReturn(adminUser);
            doNothing().when(configurationRepository).persist(any(ConfigurationEntity.class));
            boolean result = configurationService.createNewConfiguration(configurationDTO);
            assertTrue(result);
        }
        @Test
        void returnsFalseOnException() {
            when(userRepository.findUserById(2L)).thenThrow(new RuntimeException());
            boolean result = configurationService.createNewConfiguration(configurationDTO);
            assertFalse(result);
        }
    }
}
