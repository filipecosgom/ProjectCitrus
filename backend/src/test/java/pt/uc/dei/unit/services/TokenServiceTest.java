package pt.uc.dei.unit.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.uc.dei.dtos.*;
import pt.uc.dei.entities.*;
import pt.uc.dei.repositories.*;
import pt.uc.dei.services.TokenService;
import pt.uc.dei.services.UserService;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {
    @Mock ActivationTokenRepository activationTokenRepository;
    @Mock TemporaryUserRepository temporaryUserRepository;
    @Mock UserRepository userRepository;
    @Mock ConfigurationRepository configurationRepository;
    @Mock UserService userService;
    @Mock PasswordResetTokenRepository passwordResetTokenRepository;
    @InjectMocks TokenService tokenService;

    private TemporaryUserDTO tempUserDTO;
    private TemporaryUserEntity tempUserEntity;
    private ActivationTokenEntity activationTokenEntity;
    private ActivationTokenDTO activationTokenDTO;
    private UserEntity userEntity;
    private PasswordResetTokenEntity passwordResetTokenEntity;
    private PasswordResetTokenDTO passwordResetTokenDTO;
    private ConfigurationEntity configurationEntity;

    @BeforeEach
    void setUp() {
        tempUserDTO = new TemporaryUserDTO();
        tempUserDTO.setEmail("test@example.com");
        tempUserEntity = new TemporaryUserEntity();
        tempUserEntity.setEmail("test@example.com");
        activationTokenEntity = new ActivationTokenEntity();
        activationTokenEntity.setTokenValue("token123");
        activationTokenEntity.setCreationDate(LocalDateTime.now());
        activationTokenEntity.setTemporaryUser(tempUserEntity);
        activationTokenDTO = new ActivationTokenDTO();
        activationTokenDTO.setTokenValue("token123");
        activationTokenDTO.setCreationDate(LocalDateTime.now());
        userEntity = new UserEntity();
        userEntity.setEmail("user@example.com");
        passwordResetTokenEntity = new PasswordResetTokenEntity();
        passwordResetTokenEntity.setTokenValue("reset123");
        passwordResetTokenEntity.setCreationDate(LocalDateTime.now());
        passwordResetTokenEntity.setUser(userEntity);
        passwordResetTokenDTO = new PasswordResetTokenDTO();
        passwordResetTokenDTO.setTokenValue("reset123");
        passwordResetTokenDTO.setCreationDate(LocalDateTime.now());
        configurationEntity = new ConfigurationEntity();
        configurationEntity.setVerificationTime(60);
    }

    @Nested
    @DisplayName("createNewActivationToken")
    class CreateNewActivationToken {
        @Test
        void createsAndPersistsToken() {
            when(temporaryUserRepository.findTemporaryUserByEmail(anyString())).thenReturn(tempUserEntity);
            doNothing().when(activationTokenRepository).persist(any());
            String token = tokenService.createNewActivationToken(tempUserDTO);
            assertNotNull(token);
        }
    }

    @Nested
    @DisplayName("createNewPasswordResetToken")
    class CreateNewPasswordResetToken {
        @Test
        void createsAndPersistsToken() {
            when(userRepository.findUserByEmail(anyString())).thenReturn(userEntity);
            when(passwordResetTokenRepository.getTokensOfUser(userEntity)).thenReturn(Collections.emptyList());
            doNothing().when(passwordResetTokenRepository).persist(any());
            String token = tokenService.createNewPasswordResetToken("user@example.com");
            assertNotNull(token);
        }
        @Test
        void removesExistingTokens() {
            when(userRepository.findUserByEmail(anyString())).thenReturn(userEntity);
            List<PasswordResetTokenEntity> tokens = List.of(passwordResetTokenEntity);
            when(passwordResetTokenRepository.getTokensOfUser(userEntity)).thenReturn(tokens);
            doNothing().when(passwordResetTokenRepository).remove(passwordResetTokenEntity);
            doNothing().when(passwordResetTokenRepository).persist(any());
            String token = tokenService.createNewPasswordResetToken("user@example.com");
            assertNotNull(token);
        }
        @Test
        void returnsNullIfUserNotFound() {
            when(userRepository.findUserByEmail(anyString())).thenReturn(null);
            assertNull(tokenService.createNewPasswordResetToken("notfound@example.com"));
        }
    }

    @Nested
    @DisplayName("renewToken")
    class RenewToken {
        @Test
        void renewsActivationToken() {
            ActivationTokenDTO tokenDTO = new ActivationTokenDTO();
            tokenDTO.setTokenValue("token123");
            when(activationTokenRepository.getTokenFromValue("token123")).thenReturn(activationTokenEntity);
            doNothing().when(activationTokenRepository).remove(activationTokenEntity);
            when(temporaryUserRepository.findTemporaryUserByEmail(anyString())).thenReturn(tempUserEntity);
            doNothing().when(activationTokenRepository).persist(any());
            String newToken = tokenService.renewToken(tempUserDTO, tokenDTO);
            assertNotNull(newToken);
        }
        @Test
        void returnsNullForUnsupportedTokenType() {
            assertNull(tokenService.renewToken(userEntity, "stringToken"));
        }
    }

    @Nested
    @DisplayName("getTemporaryUserFromActivationToken")
    class GetTemporaryUserFromActivationToken {
        @Test
        void returnsTemporaryUserDTO() {
            when(activationTokenRepository.getTokenFromValue(anyString())).thenReturn(activationTokenEntity);
            when(temporaryUserRepository.findTemporaryUserByEmail(anyString())).thenReturn(tempUserEntity);
            when(userService.temporaryUserEntityToTemporaryUserDTO(tempUserEntity)).thenReturn(tempUserDTO);
            TemporaryUserDTO result = tokenService.getTemporaryUserFromActivationToken(activationTokenDTO);
            assertNotNull(result);
        }
        @Test
        void returnsNullIfTokenValueNull() {
            ActivationTokenDTO dto = new ActivationTokenDTO();
            dto.setTokenValue(null);
            assertNull(tokenService.getTemporaryUserFromActivationToken(dto));
        }
        @Test
        void returnsNullIfTokenNotFound() {
            when(activationTokenRepository.getTokenFromValue(anyString())).thenReturn(null);
            assertNull(tokenService.getTemporaryUserFromActivationToken(activationTokenDTO));
        }
    }

    @Nested
    @DisplayName("getActivationTokenByValue")
    class GetActivationTokenByValue {
        @Test
        void returnsActivationTokenDTO() {
            when(activationTokenRepository.getTokenFromValue(anyString())).thenReturn(activationTokenEntity);
            ActivationTokenDTO result = tokenService.getActivationTokenByValue(activationTokenDTO);
            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("getPasswordResetTokenByValue")
    class GetPasswordResetTokenByValue {
        @Test
        void returnsPasswordResetTokenDTO() {
            when(passwordResetTokenRepository.getTokenFromValue(anyString())).thenReturn(passwordResetTokenEntity);
            PasswordResetTokenDTO result = tokenService.getPasswordResetTokenByValue(passwordResetTokenDTO);
            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("isTokenExpired")
    class IsTokenExpired {
        @Test
        void returnsTrueIfExpired() {
            when(configurationRepository.getLatestConfiguration()).thenReturn(configurationEntity);
            ActivationTokenDTO token = new ActivationTokenDTO();
            token.setCreationDate(LocalDateTime.now().minusMinutes(120));
            assertTrue(tokenService.isTokenExpired(token));
        }
        @Test
        void returnsFalseIfNotExpired() {
            when(configurationRepository.getLatestConfiguration()).thenReturn(configurationEntity);
            ActivationTokenDTO token = new ActivationTokenDTO();
            token.setCreationDate(LocalDateTime.now());
            assertFalse(tokenService.isTokenExpired(token));
        }
        @Test
        void returnsFalseIfException() {
            when(configurationRepository.getLatestConfiguration()).thenThrow(new RuntimeException());
            ActivationTokenDTO token = new ActivationTokenDTO();
            token.setCreationDate(LocalDateTime.now());
            assertFalse(tokenService.isTokenExpired(token));
        }
    }

    @Nested
    @DisplayName("generateNewToken")
    class GenerateNewToken {
        @Test
        void generatesUniqueToken() {
            String token1 = tokenService.generateNewToken();
            String token2 = tokenService.generateNewToken();
            assertNotNull(token1);
            assertNotNull(token2);
            assertNotEquals(token1, token2);
            assertTrue(token1.length() > 0);
            assertTrue(token2.length() > 0);
        }
        @Test
        void tokenIsUrlSafeBase64() {
            String token = tokenService.generateNewToken();
            assertDoesNotThrow(() -> Base64.getUrlDecoder().decode(token));
        }
    }
}
