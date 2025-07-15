package pt.uc.dei.unit.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.uc.dei.dtos.*;
import pt.uc.dei.entities.*;
import pt.uc.dei.enums.AccountState;
import pt.uc.dei.mapper.UserMapper;
import pt.uc.dei.repositories.*;
import pt.uc.dei.services.AuthenticationService;
import pt.uc.dei.utils.JWTUtil;
import pt.uc.dei.utils.PasswordUtils;
import pt.uc.dei.utils.TwoFactorUtil;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    @Mock UserRepository userRepository;
    @Mock JWTUtil jwtUtil;
    @Mock UserMapper userMapper;
    @Mock ActivationTokenRepository activationTokenRepository;
    @Mock PasswordResetTokenRepository passwordResetTokenRepository;
    @InjectMocks AuthenticationService authenticationService;

    private UserEntity user;
    private LoginDTO loginDTO;
    private UserResponseDTO userResponseDTO;
    private RequestAuthCodeDTO requestAuthCodeDTO;
    private TemporaryUserDTO temporaryUserDTO;
    private PasswordResetTokenDTO passwordResetTokenDTO;
    private PasswordResetTokenEntity passwordResetTokenEntity;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("hashed");
        user.setSecretKey("SECRET");
        user.setOnlineStatus(false);
        user.setAccountState(AccountState.INCOMPLETE);
        user.setCreationDate(LocalDateTime.now());
        user.setUserIsManager(false);
        user.setHasAvatar(false);
        user.setLastSeen(LocalDateTime.now());

        loginDTO = new LoginDTO();
        loginDTO.setEmail("test@example.com");
        loginDTO.setPassword("plain");
        loginDTO.setAuthenticationCode("123456");

        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setEmail("test@example.com");

        requestAuthCodeDTO = new RequestAuthCodeDTO();
        requestAuthCodeDTO.setEmail("test@example.com");
        requestAuthCodeDTO.setPassword("plain");

        temporaryUserDTO = new TemporaryUserDTO();
        temporaryUserDTO.setEmail("temp@example.com");
        temporaryUserDTO.setPassword("temppass");
        temporaryUserDTO.setSecretKey("TEMPSECRET");

        passwordResetTokenDTO = new PasswordResetTokenDTO();
        passwordResetTokenDTO.setTokenValue("token123");
        passwordResetTokenEntity = new PasswordResetTokenEntity();
        passwordResetTokenEntity.setTokenValue("token123");
        passwordResetTokenEntity.setUser(user);
    }

    @Nested
    @DisplayName("loginUser")
    class LoginUser {

        @Test
        void returnsNullOnInvalidPassword() {
            when(userRepository.findUserByEmail("test@example.com")).thenReturn(user);
            try (var passwordUtilsMock = mockStatic(PasswordUtils.class)) {
                passwordUtilsMock.when(() -> PasswordUtils.verify(anyString(), anyString())).thenReturn(false);
                String token = authenticationService.loginUser(loginDTO);
                assertNull(token);
            }
        }
        @Test
        void returnsNullOnUserNotFound() {
            when(userRepository.findUserByEmail("test@example.com")).thenReturn(null);
            String token = authenticationService.loginUser(loginDTO);
            assertNull(token);
        }
    }

    @Nested
    @DisplayName("logoutUser")
    class LogoutUser {
        @Test
        void returnsTrueIfUserSetOffline() {
            AuthenticationService spy = Mockito.spy(authenticationService);
            doReturn(true).when(spy).setUserOffline(1L);
            assertTrue(spy.logoutUser(1L));
        }
        @Test
        void returnsFalseIfUserNotSetOffline() {
            AuthenticationService spy = Mockito.spy(authenticationService);
            doReturn(false).when(spy).setUserOffline(1L);
            assertFalse(spy.logoutUser(1L));
        }
    }

    @Nested
    @DisplayName("checkAuthenticationCode")
    class CheckAuthenticationCode {
        @Test
        void returnsTrueIfCodeValidAndMatches() {
            when(userRepository.findUserByEmail("test@example.com")).thenReturn(user);
            try (var twoFactorUtilMock = mockStatic(TwoFactorUtil.class)) {
                twoFactorUtilMock.when(() -> TwoFactorUtil.validateCode("123456")).thenReturn(true);
                twoFactorUtilMock.when(() -> TwoFactorUtil.verifyTwoFactorCode("SECRET", "123456")).thenReturn(true);
                assertTrue(authenticationService.checkAuthenticationCode(loginDTO));
            }
        }
        @Test
        void returnsFalseIfCodeInvalid() {
            when(userRepository.findUserByEmail("test@example.com")).thenReturn(user);
            try (var twoFactorUtilMock = mockStatic(TwoFactorUtil.class)) {
                twoFactorUtilMock.when(() -> TwoFactorUtil.validateCode("123456")).thenReturn(false);
                assertFalse(authenticationService.checkAuthenticationCode(loginDTO));
            }
        }
        @Test
        void returnsFalseIfCodeDoesNotMatch() {
            when(userRepository.findUserByEmail("test@example.com")).thenReturn(user);
            try (var twoFactorUtilMock = mockStatic(TwoFactorUtil.class)) {
                twoFactorUtilMock.when(() -> TwoFactorUtil.validateCode("123456")).thenReturn(true);
                twoFactorUtilMock.when(() -> TwoFactorUtil.verifyTwoFactorCode("SECRET", "123456")).thenReturn(false);
                assertFalse(authenticationService.checkAuthenticationCode(loginDTO));
            }
        }
    }

    @Nested
    @DisplayName("getSelfInformation")
    class GetSelfInformation {
        @Test
        void returnsUserResponseDTOIfFound() {
            when(userRepository.findUserById(1L)).thenReturn(user);
            when(userMapper.toUserResponseDto(user)).thenReturn(userResponseDTO);
            assertEquals(userResponseDTO, authenticationService.getSelfInformation(1L));
        }
        @Test
        void returnsNullIfUserNotFound() {
            when(userRepository.findUserById(1L)).thenReturn(null);
            assertNull(authenticationService.getSelfInformation(1L));
        }
    }

    @Nested
    @DisplayName("getAuthCode")
    class GetAuthCode {
        @Test
        void returnsAuthCodeIfCredentialsValid() {
            when(userRepository.findUserByEmail("test@example.com")).thenReturn(user);
            try (var passwordUtilsMock = mockStatic(PasswordUtils.class)) {
                passwordUtilsMock.when(() -> PasswordUtils.verify("hashed", "plain")).thenReturn(true);
                user.setPassword("hashed");
                user.setSecretKey("SECRET");
                requestAuthCodeDTO.setPassword("plain");
                assertEquals("SECRET", authenticationService.getAuthCode(requestAuthCodeDTO));
            }
        }
        @Test
        void returnsNullIfUserNotFound() {
            when(userRepository.findUserByEmail("test@example.com")).thenReturn(null);
            assertNull(authenticationService.getAuthCode(requestAuthCodeDTO));
        }
        @Test
        void returnsNullIfPasswordInvalid() {
            when(userRepository.findUserByEmail("test@example.com")).thenReturn(user);
            try (var passwordUtilsMock = mockStatic(PasswordUtils.class)) {
                passwordUtilsMock.when(() -> PasswordUtils.verify(anyString(), anyString())).thenReturn(false);
                assertNull(authenticationService.getAuthCode(requestAuthCodeDTO));
            }
        }
    }

    @Nested
    @DisplayName("activateUser")
    class ActivateUser {
        @Test
        void returnsTrueOnSuccess() {
            doNothing().when(userRepository).persist(any(UserEntity.class));
            assertTrue(authenticationService.activateUser(temporaryUserDTO));
        }
        @Test
        void returnsFalseOnException() {
            doThrow(new RuntimeException()).when(userRepository).persist(any(UserEntity.class));
            assertFalse(authenticationService.activateUser(temporaryUserDTO));
        }
    }

    @Nested
    @DisplayName("setNewPassword")
    class SetNewPassword {
        @Test
        void returnsTrueOnSuccess() {
            when(passwordResetTokenRepository.getTokenFromValue("token123")).thenReturn(passwordResetTokenEntity);
            when(userRepository.findUserById(1L)).thenReturn(user);
            try (var passwordUtilsMock = mockStatic(PasswordUtils.class)) {
                passwordUtilsMock.when(() -> PasswordUtils.encrypt("newpass")).thenReturn("hashednew");
                doNothing().when(userRepository).persist(user);
                doNothing().when(passwordResetTokenRepository).remove(passwordResetTokenEntity);
                assertTrue(authenticationService.setNewPassword(passwordResetTokenDTO, "newpass"));
            }
        }
        @Test
        void returnsFalseOnException() {
            when(passwordResetTokenRepository.getTokenFromValue("token123")).thenThrow(new RuntimeException());
            assertFalse(authenticationService.setNewPassword(passwordResetTokenDTO, "newpass"));
        }
    }

    @Nested
    @DisplayName("setUserOnline and setUserOffline")
    class SetUserOnlineOffline {
        @Test
        void setUserOnlineReturnsTrueIfUserFound() {
            when(userRepository.findUserById(1L)).thenReturn(user);
            doNothing().when(userRepository).merge(user);
            assertTrue(authenticationService.setUserOnline(1L));
        }
        @Test
        void setUserOnlineReturnsFalseIfUserNotFound() {
            when(userRepository.findUserById(1L)).thenReturn(null);
            assertFalse(authenticationService.setUserOnline(1L));
        }
        @Test
        void setUserOfflineReturnsTrueIfUserFound() {
            when(userRepository.findUserById(1L)).thenReturn(user);
            doNothing().when(userRepository).merge(user);
            assertTrue(authenticationService.setUserOffline(1L));
        }
        @Test
        void setUserOfflineReturnsFalseIfUserNotFound() {
            when(userRepository.findUserById(1L)).thenReturn(null);
            assertFalse(authenticationService.setUserOffline(1L));
        }
    }
}
