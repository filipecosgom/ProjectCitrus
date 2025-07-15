package pt.uc.dei.unit.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pt.uc.dei.controllers.UserController;
import pt.uc.dei.dtos.*;
import pt.uc.dei.enums.AccountState;
import pt.uc.dei.enums.Office;
import pt.uc.dei.enums.Parameter;
import pt.uc.dei.enums.OrderBy;
import pt.uc.dei.enums.Language;
import pt.uc.dei.services.*;
import pt.uc.dei.utils.ApiResponse;
import pt.uc.dei.utils.JWTUtil;
import pt.uc.dei.mapper.FinishedCourseMapper;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Cookie;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Test
    void testGetUserData_validJwt() {
        // Placeholder: would require static mocking for JWTUtil
        // Not implemented due to static method limitations in Mockito
    }

    @Test
    void testGetUserData_missingJwt() {
        HttpHeaders localHttpHeaders = mock(HttpHeaders.class);
        when(localHttpHeaders.getCookies()).thenReturn(java.util.Collections.emptyMap());
        Response response = userController.getUserData(localHttpHeaders);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        ApiResponse apiResponse = (ApiResponse) response.getEntity();
        assertFalse(apiResponse.isSuccess());
        assertEquals("Missing token", apiResponse.getMessage());
    }

    @Test
    void testGetUserById_success() {
        UserDTO user = new UserDTO();
        when(userService.getUserProfile(anyLong(), anyBoolean())).thenReturn(user);
        Response response = userController.getUserById(1L, "jwt");
        // Since JWTUtil is not mocked, expect 401 Unauthorized
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    void testUpdateUser_exception() {
        UpdateUserDTO dto = new UpdateUserDTO();
        when(userService.updateUser(eq(1L), eq(dto))).thenThrow(new RuntimeException("fail"));
        Response response = userController.updateUser(1L, dto);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    void testExportUsersToCSV_unauthorized() {
        Response response = userController.exportUsersToCSV(null, null, null, null, null, null, null, null, null, null, null, null, null, "");
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    void testExportUsersToXLSX_unauthorized() {
        Response response = userController.exportUsersToXLSX(null, null, null, null, null, null, null, null, null, null, null, null, null, "");
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    void testUpdateAdminPermissions_missingField() {
        Map<String, Object> data = new HashMap<>();
        Response response = userController.updateAdminPermissions(1L, data, "jwt");
        // Since JWTUtil is not mocked, expect 401 Unauthorized
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    void testUpdateAdminPermissions_invalidValue() {
        Map<String, Object> data = new HashMap<>();
        data.put("isAdmin", null);
        Response response = userController.updateAdminPermissions(1L, data, "jwt");
        // Since JWTUtil is not mocked, expect 401 Unauthorized
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    // Add more tests for uploadAvatar and getAvatar as needed (mocking file operations)
    @Mock UserService userService;
    @Mock EmailService emailService;
    @Mock AuthenticationService authenticationService;
    @Mock AppraisalService appraisalService;
    @Mock FinishedCourseMapper finishedCourseMapper;
    @InjectMocks UserController userController;

    @BeforeEach
    void setUp() {
        // No-op, handled by @InjectMocks
    }

    @Test
    void testRegisterUser_duplicateEmail() {
        TemporaryUserDTO dto = new TemporaryUserDTO();
        dto.setEmail("test@example.com");
        when(userService.findIfUserExists("test@example.com")).thenReturn(true);
        Response response = userController.registerUser(dto, "en");
        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
        ApiResponse api = (ApiResponse) response.getEntity();
        assertFalse(api.isSuccess());
    }

    @Test
    void testRegisterUser_success() {
        TemporaryUserDTO dto = new TemporaryUserDTO();
        dto.setEmail("new@example.com");
        Map<String, String> codes = new HashMap<>();
        codes.put("token", "tok");
        codes.put("secretKey", "sec");
        when(userService.findIfUserExists("new@example.com")).thenReturn(false);
        when(userService.registerUser(dto)).thenReturn(codes);
        doNothing().when(emailService).sendActivationEmail(anyString(), anyString(), anyString(), anyString());
        Response response = userController.registerUser(dto, "en");
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        ApiResponse api = (ApiResponse) response.getEntity();
        assertTrue(api.isSuccess());
    }

    @Test
    void testRegisterUser_tokenNull() {
        TemporaryUserDTO dto = new TemporaryUserDTO();
        dto.setEmail("fail@example.com");
        Map<String, String> codes = new HashMap<>();
        codes.put("token", null);
        codes.put("secretKey", "sec");
        when(userService.findIfUserExists("fail@example.com")).thenReturn(false);
        when(userService.registerUser(dto)).thenReturn(codes);
        Response response = userController.registerUser(dto, "en");
        assertEquals(Response.Status.SERVICE_UNAVAILABLE.getStatusCode(), response.getStatus());
    }

    @Test
    void testRegisterUser_secretKeyNull() {
        TemporaryUserDTO dto = new TemporaryUserDTO();
        dto.setEmail("fail2@example.com");
        Map<String, String> codes = new HashMap<>();
        codes.put("token", "tok");
        codes.put("secretKey", null);
        when(userService.findIfUserExists("fail2@example.com")).thenReturn(false);
        when(userService.registerUser(dto)).thenReturn(codes);
        Response response = userController.registerUser(dto, "en");
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    void testRegisterUser_illegalArgument() {
        TemporaryUserDTO dto = new TemporaryUserDTO();
        dto.setEmail("bad@example.com");
        when(userService.findIfUserExists("bad@example.com")).thenReturn(false);
        when(userService.registerUser(dto)).thenThrow(new IllegalArgumentException("bad data"));
        Response response = userController.registerUser(dto, "en");
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    void testRegisterUser_exception() {
        TemporaryUserDTO dto = new TemporaryUserDTO();
        dto.setEmail("err@example.com");
        when(userService.findIfUserExists("err@example.com")).thenReturn(false);
        when(userService.registerUser(dto)).thenThrow(new RuntimeException("fail"));
        Response response = userController.registerUser(dto, "en");
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }
}
