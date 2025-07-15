package pt.uc.dei.unit.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pt.uc.dei.controllers.NotificationController;
import pt.uc.dei.dtos.NotificationDTO;
import pt.uc.dei.dtos.NotificationUpdateDTO;
import pt.uc.dei.services.NotificationService;
import pt.uc.dei.utils.ApiResponse;
import pt.uc.dei.utils.JWTUtil;

import jakarta.ws.rs.core.Response;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {
    @Mock NotificationService notificationService;
    @InjectMocks NotificationController notificationController;

    @BeforeEach
    void setUp() {
        // No-op, handled by @InjectMocks
    }

    @Test
    void testGetNotifications_unauthorized() {
        Response response = notificationController.getNotifications(null);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        ApiResponse api = (ApiResponse) response.getEntity();
        assertFalse(api.isSuccess());
    }

    @Test
    void testGetNotifications_emptyList() {
        try (var mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            when(notificationService.getNotifications(1L)).thenReturn(Collections.emptyList());
            Response response = notificationController.getNotifications("jwt");
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            ApiResponse api = (ApiResponse) response.getEntity();
            assertTrue(api.isSuccess());
            assertNotNull(api.getData());
        }
    }

    @Test
    void testGetNotifications_success() {
        try (var mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.getUserIdFromToken(anyString())).thenReturn(2L);
            List<NotificationDTO> notifications = List.of(new NotificationDTO());
            when(notificationService.getNotifications(2L)).thenReturn(notifications);
            Response response = notificationController.getNotifications("jwt");
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            ApiResponse api = (ApiResponse) response.getEntity();
            assertTrue(api.isSuccess());
            assertEquals(notifications, api.getData());
        }
    }

    @Test
    void testGetNotifications_exception() {
        try (var mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.getUserIdFromToken(anyString())).thenReturn(3L);
            when(notificationService.getNotifications(3L)).thenThrow(new RuntimeException("fail"));
            Response response = notificationController.getNotifications("jwt");
            assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
            ApiResponse api = (ApiResponse) response.getEntity();
            assertFalse(api.isSuccess());
        }
    }

    @Test
    void testUpdateNotificationStatus_unauthorized() {
        Response response = notificationController.updateNotificationStatus(null, new NotificationUpdateDTO());
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    void testUpdateNotificationStatus_invalidDTO() {
        try (var mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            Response response = notificationController.updateNotificationStatus("jwt", null);
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testUpdateNotificationStatus_notFound() {
        try (var mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            NotificationUpdateDTO dto = new NotificationUpdateDTO();
            dto.setNotificationId(10L);
            when(notificationService.updateNotificationStatus(dto, 1L)).thenReturn(false);
            Response response = notificationController.updateNotificationStatus("jwt", dto);
            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testUpdateNotificationStatus_success() {
        try (var mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            NotificationUpdateDTO dto = new NotificationUpdateDTO();
            dto.setNotificationId(11L);
            when(notificationService.updateNotificationStatus(dto, 1L)).thenReturn(true);
            Response response = notificationController.updateNotificationStatus("jwt", dto);
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testUpdateNotificationStatus_exception() {
        try (var mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            NotificationUpdateDTO dto = new NotificationUpdateDTO();
            dto.setNotificationId(12L);
            when(notificationService.updateNotificationStatus(dto, 1L)).thenThrow(new RuntimeException("fail"));
            Response response = notificationController.updateNotificationStatus("jwt", dto);
            assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testMarkMessageNotificationsAsRead_unauthorized() {
        Response response = notificationController.markMessageNotificationsAsRead(null);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    void testMarkMessageNotificationsAsRead_success() {
        try (var mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            when(notificationService.markMessageNotificationsAsRead(1L)).thenReturn(true);
            Response response = notificationController.markMessageNotificationsAsRead("jwt");
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testMarkMessageNotificationsAsRead_failure() {
        try (var mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            when(notificationService.markMessageNotificationsAsRead(1L)).thenReturn(false);
            Response response = notificationController.markMessageNotificationsAsRead("jwt");
            assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testMarkMessageNotificationsAsRead_exception() {
        try (var mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            when(notificationService.markMessageNotificationsAsRead(1L)).thenThrow(new RuntimeException("fail"));
            Response response = notificationController.markMessageNotificationsAsRead("jwt");
            assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        }
    }
}
