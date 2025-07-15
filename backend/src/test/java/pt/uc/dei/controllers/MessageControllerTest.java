package pt.uc.dei.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.uc.dei.dtos.*;
import pt.uc.dei.services.MessageService;
import pt.uc.dei.services.NotificationService;
import pt.uc.dei.services.UserService;
import pt.uc.dei.utils.ApiResponse;
import pt.uc.dei.utils.JWTUtil;

import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageControllerTest {
    @Mock UserService userService;
    @Mock MessageService messageService;
    @Mock NotificationService notificationService;
    @InjectMocks MessageController messageController;

    @BeforeEach
    void setUp() {
        // No-op, handled by @InjectMocks
    }

    @Test
    void testGetChat_unauthorized_missingJwt() {
        Response response = messageController.getChat(1L, null);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetChat_unauthorized_invalidJwt() {
        try (var mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.getUserIdFromToken(anyString())).thenReturn(null);
            Response response = messageController.getChat(1L, "jwt");
            assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testGetChat_notFound() {
        try (var mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            when(messageService.getMessagesBetween(1L, 2L)).thenReturn(Collections.emptyList());
            Response response = messageController.getChat(2L, "jwt");
            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testGetChat_success() {
        try (var mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            List<MessageDTO> messages = List.of(new MessageDTO());
            when(messageService.getMessagesBetween(1L, 2L)).thenReturn(messages);
            Response response = messageController.getChat(2L, "jwt");
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            ApiResponse api = (ApiResponse) response.getEntity();
            assertTrue(api.isSuccess());
            assertEquals(messages, api.getData());
        }
    }

    @Test
    void testGetAllUsersWhoChat_unauthorized() {
        Response response = messageController.getAllUsersWhoChat(null);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetAllUsersWhoChat_notFound() {
        try (var mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            when(messageService.getAllChats(1L)).thenReturn(Collections.emptyList());
            Response response = messageController.getAllUsersWhoChat("jwt");
            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testGetAllUsersWhoChat_success() {
        try (var mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            List<UserResponseDTO> users = List.of(new UserResponseDTO());
            when(messageService.getAllChats(1L)).thenReturn(users);
            Response response = messageController.getAllUsersWhoChat("jwt");
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            ApiResponse api = (ApiResponse) response.getEntity();
            assertTrue(api.isSuccess());
            assertEquals(users, api.getData());
        }
    }

    @Test
    void testSendMessage_unauthorized() {
        Response response = messageController.sendMessage(null, new MessageSendDTO());
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    void testSendMessage_sameUser() {
        try (var mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            MessageSendDTO dto = new MessageSendDTO();
            dto.setReceiverId(1L);
            Response response = messageController.sendMessage("jwt", dto);
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testSendMessage_internalError() {
        try (var mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            MessageSendDTO dto = new MessageSendDTO();
            dto.setReceiverId(2L);
            dto.setContent("Hello");
            when(messageService.newMessage(any())).thenReturn(null);
            Response response = messageController.sendMessage("jwt", dto);
            assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testSendMessage_success() {
        try (var mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            MessageSendDTO dto = new MessageSendDTO();
            dto.setReceiverId(2L);
            dto.setContent("Hello");
            MessageDTO saved = new MessageDTO();
            when(messageService.newMessage(any())).thenReturn(saved);
            Response response = messageController.sendMessage("jwt", dto);
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            ApiResponse api = (ApiResponse) response.getEntity();
            assertTrue(api.isSuccess());
            assertEquals(saved, api.getData());
        }
    }

    @Test
    void testReadConversation_unauthorized() {
        Response response = messageController.readConversation(null, 1L);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    void testReadConversation_missingSenderId() {
        try (var mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            Response response = messageController.readConversation("jwt", null);
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testReadConversation_success() {
        try (var mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            when(messageService.readAllConversation(1L, 2L)).thenReturn(true);
            Response response = messageController.readConversation("jwt", 2L);
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testReadConversation_notFound() {
        try (var mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            when(messageService.readAllConversation(1L, 2L)).thenReturn(false);
            Response response = messageController.readConversation("jwt", 2L);
            assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testReadConversation_exception() {
        try (var mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            when(messageService.readAllConversation(1L, 2L)).thenThrow(new RuntimeException("fail"));
            Response response = messageController.readConversation("jwt", 2L);
            assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        }
    }

    @Test
    void testGetConversationPreviews_unauthorized() {
        Response response = messageController.getConversationPreviews(null);
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());
    }

    @Test
    void testGetConversationPreviews_empty() {
        try (var mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            when(messageService.getConversationPreviews(1L)).thenReturn(Collections.emptyList());
            Response response = messageController.getConversationPreviews("jwt");
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            ApiResponse api = (ApiResponse) response.getEntity();
            assertTrue(api.isSuccess());
            assertTrue(((List<?>) api.getData()).isEmpty());
        }
    }

    @Test
    void testGetConversationPreviews_success() {
        try (var mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            List<ConversationPreviewDTO> previews = List.of(new ConversationPreviewDTO());
            when(messageService.getConversationPreviews(1L)).thenReturn(previews);
            Response response = messageController.getConversationPreviews("jwt");
            assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
            ApiResponse api = (ApiResponse) response.getEntity();
            assertTrue(api.isSuccess());
            assertEquals(previews, api.getData());
        }
    }

    @Test
    void testGetConversationPreviews_exception() {
        try (var mocked = mockStatic(JWTUtil.class)) {
            mocked.when(() -> JWTUtil.getUserIdFromToken(anyString())).thenReturn(1L);
            when(messageService.getConversationPreviews(1L)).thenThrow(new RuntimeException("fail"));
            Response response = messageController.getConversationPreviews("jwt");
            assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        }
    }
}
