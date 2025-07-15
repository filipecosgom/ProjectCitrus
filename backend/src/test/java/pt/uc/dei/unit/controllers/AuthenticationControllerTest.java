package pt.uc.dei.unit.controllers;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;

import pt.uc.dei.controllers.AuthenticationController;
import pt.uc.dei.dtos.*;
import pt.uc.dei.services.*;
import pt.uc.dei.utils.ApiResponse;
import pt.uc.dei.utils.JWTUtil;
import pt.uc.dei.utils.TwoFactorUtil;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {
    @Mock AuthenticationService authenticationService;
    @Mock UserService userService;
    @Mock TokenService tokenService;
    @Mock ConfigurationService configurationService;
    @Mock EmailService emailService;
    @InjectMocks AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        // No-op, handled by @InjectMocks
    }

    @Test
    void testLogin_success_no2fa() {
        LoginDTO login = new LoginDTO();
        login.setEmail("user@example.com");
        login.setPassword("pass");
        ConfigurationDTO config = new ConfigurationDTO();
        config.setTwoFactorAuthEnabled(false);
        config.setLoginTime(30);
        when(configurationService.getLatestConfiguration()).thenReturn(config);
        when(authenticationService.loginUser(login)).thenReturn("token");
        Response response = authenticationController.login(login);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        ApiResponse api = (ApiResponse) response.getEntity();
        assertTrue(api.isSuccess());
        assertEquals("token", ((Map<?,?>)api.getData()).get("token"));
    }

    @Test
    void testLogin_success_with2fa() {
        LoginDTO login = new LoginDTO();
        login.setEmail("user@example.com");
        login.setPassword("pass");
        login.setAuthenticationCode("123456");
        ConfigurationDTO config = new ConfigurationDTO();
        config.setTwoFactorAuthEnabled(true);
        config.setLoginTime(30);
        when(configurationService.getLatestConfiguration()).thenReturn(config);
        try (MockedStatic<TwoFactorUtil> mocked2fa = mockStatic(TwoFactorUtil.class)) {
            mocked2fa.when(() -> TwoFactorUtil.validateCode(anyString())).thenReturn(true);
            when(authenticationService.checkAuthenticationCode(login)).thenReturn(true);
            when(authenticationService.loginUser(login)).thenReturn("token");
            Response response = authenticationController.login(login);
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testLogin_invalid2faCode() {
        LoginDTO login = new LoginDTO();
        login.setAuthenticationCode("bad");
        ConfigurationDTO config = new ConfigurationDTO();
        config.setTwoFactorAuthEnabled(true);
        when(configurationService.getLatestConfiguration()).thenReturn(config);
        try (MockedStatic<TwoFactorUtil> mocked2fa = mockStatic(TwoFactorUtil.class)) {
            mocked2fa.when(() -> TwoFactorUtil.validateCode(anyString())).thenReturn(false);
            Response response = authenticationController.login(login);
            assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testLogin_invalid2faAuthService() {
        LoginDTO login = new LoginDTO();
        login.setAuthenticationCode("123456");
        ConfigurationDTO config = new ConfigurationDTO();
        config.setTwoFactorAuthEnabled(true);
        when(configurationService.getLatestConfiguration()).thenReturn(config);
        try (MockedStatic<TwoFactorUtil> mocked2fa = mockStatic(TwoFactorUtil.class)) {
            mocked2fa.when(() -> TwoFactorUtil.validateCode(anyString())).thenReturn(true);
            when(authenticationService.checkAuthenticationCode(login)).thenReturn(false);
            Response response = authenticationController.login(login);
            assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testLogin_invalidCredentials() {
        LoginDTO login = new LoginDTO();
        ConfigurationDTO config = new ConfigurationDTO();
        config.setTwoFactorAuthEnabled(false);
        when(configurationService.getLatestConfiguration()).thenReturn(config);
        when(authenticationService.loginUser(login)).thenReturn(null);
        Response response = authenticationController.login(login);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    void testLogout_success() {
        try (MockedStatic<JWTUtil> mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            when(authenticationService.logoutUser(1L)).thenReturn(true);
            HttpServletResponse servletResponse = mock(HttpServletResponse.class);
            Response response = authenticationController.logout(servletResponse, "jwt");
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testLogout_unauthorized_missingJwt() {
        HttpServletResponse servletResponse = mock(HttpServletResponse.class);
        Response response = authenticationController.logout(servletResponse, null);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    void testLogout_unauthorized_invalidJwt() {
        try (MockedStatic<JWTUtil> mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.getUserIdFromToken(anyString())).thenReturn(null);
            HttpServletResponse servletResponse = mock(HttpServletResponse.class);
            Response response = authenticationController.logout(servletResponse, "jwt");
            assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testLogout_failure() {
        try (MockedStatic<JWTUtil> mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            when(authenticationService.logoutUser(1L)).thenReturn(false);
            HttpServletResponse servletResponse = mock(HttpServletResponse.class);
            Response response = authenticationController.logout(servletResponse, "jwt");
            assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testRequestPasswordReset_missingLanguage() {
        JsonObject emailJson = Json.createObjectBuilder().add("email", "user@example.com").build();
        Response response = authenticationController.requestPasswordReset(emailJson, "");
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void testRequestPasswordReset_missingEmail() {
        JsonObject emailJson = Json.createObjectBuilder().build();
        Response response = authenticationController.requestPasswordReset(emailJson, "en");
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void testRequestPasswordReset_userNotExists() {
        JsonObject emailJson = Json.createObjectBuilder().add("email", "user@example.com").build();
        when(userService.findIfUserExists("user@example.com")).thenReturn(false);
        Response response = authenticationController.requestPasswordReset(emailJson, "en");
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    @Test
    void testRequestPasswordReset_tokenNull() {
        JsonObject emailJson = Json.createObjectBuilder().add("email", "user@example.com").build();
        when(userService.findIfUserExists("user@example.com")).thenReturn(true);
        when(tokenService.createNewPasswordResetToken("user@example.com")).thenReturn(null);
        Response response = authenticationController.requestPasswordReset(emailJson, "en");
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    void testRequestPasswordReset_success() {
        JsonObject emailJson = Json.createObjectBuilder().add("email", "user@example.com").build();
        when(userService.findIfUserExists("user@example.com")).thenReturn(true);
        when(tokenService.createNewPasswordResetToken("user@example.com")).thenReturn("token");
        doNothing().when(emailService).sendPasswordResetEmail(anyString(), anyString(), anyString());
        Response response = authenticationController.requestPasswordReset(emailJson, "en");
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    }

    @Test
    void testRequestPasswordReset_exception() {
        JsonObject emailJson = Json.createObjectBuilder().add("email", "user@example.com").build();
        when(userService.findIfUserExists("user@example.com")).thenReturn(true);
        when(tokenService.createNewPasswordResetToken("user@example.com")).thenThrow(new RuntimeException("fail"));
        Response response = authenticationController.requestPasswordReset(emailJson, "en");
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    void testCheckPasswordReset_missingToken() {
        Response response = authenticationController.checkPasswordReset("");
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void testCheckPasswordReset_expired() {
        PasswordResetTokenDTO dto = new PasswordResetTokenDTO("token");
        when(tokenService.getPasswordResetTokenByValue(any())).thenReturn(dto);
        when(tokenService.isTokenExpired(dto)).thenReturn(true);
        Response response = authenticationController.checkPasswordReset("token");
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    void testCheckPasswordReset_valid() {
        PasswordResetTokenDTO dto = new PasswordResetTokenDTO("token");
        when(tokenService.getPasswordResetTokenByValue(any())).thenReturn(dto);
        when(tokenService.isTokenExpired(dto)).thenReturn(false);
        Response response = authenticationController.checkPasswordReset("token");
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testUpdatePassword_missingPassword() {
        JsonObject json = Json.createObjectBuilder().build();
        Response response = authenticationController.updatePassword("token", json);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void testUpdatePassword_missingToken() {
        JsonObject json = Json.createObjectBuilder().add("password", "newpass").build();
        Response response = authenticationController.updatePassword("", json);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void testUpdatePassword_expiredToken() {
        JsonObject json = Json.createObjectBuilder().add("password", "newpass").build();
        PasswordResetTokenDTO dto = new PasswordResetTokenDTO("token");
        when(tokenService.getPasswordResetTokenByValue(any())).thenReturn(dto);
        when(tokenService.isTokenExpired(dto)).thenReturn(true);
        Response response = authenticationController.updatePassword("token", json);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    void testUpdatePassword_success() {
        JsonObject json = Json.createObjectBuilder().add("password", "newpass").build();
        PasswordResetTokenDTO dto = new PasswordResetTokenDTO("token");
        when(tokenService.getPasswordResetTokenByValue(any())).thenReturn(dto);
        when(tokenService.isTokenExpired(dto)).thenReturn(false);
        when(authenticationService.setNewPassword(dto, "newpass")).thenReturn(true);
        Response response = authenticationController.updatePassword("token", json);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testUpdatePassword_failure() {
        JsonObject json = Json.createObjectBuilder().add("password", "newpass").build();
        PasswordResetTokenDTO dto = new PasswordResetTokenDTO("token");
        when(tokenService.getPasswordResetTokenByValue(any())).thenReturn(dto);
        when(tokenService.isTokenExpired(dto)).thenReturn(false);
        when(authenticationService.setNewPassword(dto, "newpass")).thenReturn(false);
        Response response = authenticationController.updatePassword("token", json);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    void testRequestAuthCode_success() {
        RequestAuthCodeDTO dto = new RequestAuthCodeDTO();
        dto.setEmail("user@example.com");
        when(authenticationService.getAuthCode(dto)).thenReturn("123456");
        Response response = authenticationController.requestAuthCode(dto);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        ApiResponse api = (ApiResponse) response.getEntity();
        assertTrue(api.isSuccess());
        assertEquals("123456", ((Map<?,?>)api.getData()).get("authCode"));
    }

    @Test
    void testRequestAuthCode_unauthorized() {
        RequestAuthCodeDTO dto = new RequestAuthCodeDTO();
        dto.setEmail("user@example.com");
        when(authenticationService.getAuthCode(dto)).thenReturn(null);
        Response response = authenticationController.requestAuthCode(dto);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    void testRequestAuthCode_exception() {
        RequestAuthCodeDTO dto = new RequestAuthCodeDTO();
        dto.setEmail("user@example.com");
        when(authenticationService.getAuthCode(dto)).thenThrow(new RuntimeException("fail"));
        Response response = authenticationController.requestAuthCode(dto);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }
}
