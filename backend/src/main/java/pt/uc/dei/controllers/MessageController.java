package pt.uc.dei.controllers;

import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.annotations.AnotherOnly;
import pt.uc.dei.dtos.MessageSendDTO;
import pt.uc.dei.dtos.UserResponseDTO;
import pt.uc.dei.services.MessageService;
import pt.uc.dei.services.NotificationService;
import pt.uc.dei.services.UserService;
import pt.uc.dei.utils.ApiResponse;
import pt.uc.dei.utils.JWTUtil;
import pt.uc.dei.dtos.MessageDTO;
import pt.uc.dei.dtos.UserDTO;

import java.time.LocalDateTime;
import java.util.List;

@Path("/messages")
public class MessageController {
    private static final Logger LOGGER = LogManager.getLogger(MessageController.class);

    @Inject
    UserService userService;

    @Inject
    MessageService messageService;

    @Inject
    NotificationService notificationService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Response getChat(@PathParam("id") Long otherUserId, @CookieParam("jwt") String jwtToken) {
        try {
            // Validate JWT cookie exists
            if (jwtToken == null || jwtToken.isEmpty()) {
                LOGGER.warn("Unauthorized chat request: missing JWT cookie");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ApiResponse(false, "Unauthorized", "errorUnauthorized", null))
                        .build();
            }

            // Extract user ID from JWT
            Long userId = JWTUtil.getUserIdFromToken(jwtToken);

            if (userId == null) {
                LOGGER.warn("Invalid JWT token in chat request");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ApiResponse(false, "Unauthorized", "errorUnauthorized", null))
                        .build();
            }

            LOGGER.info("Request to get chat with userId: {} and otherUserId: {}", userId, otherUserId);
        List<MessageDTO> conversation = messageService.getMessagesBetween(userId, otherUserId);
        if (conversation == null || conversation.isEmpty()) {
            LOGGER.info("No conversation found between userId {} and otherUserId {}", userId, otherUserId);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiResponse(false, "No conversation found", "errorNoConversation", null))
                    .build();
        }
            LOGGER.info("Conversation retrieved between userId {} and otherUserId {}", userId, otherUserId);
            return Response.ok(new ApiResponse(true, "Conversation retrieved", "successConversationRetrieved", conversation))
                    .build();

        } catch (Exception e) {
            LOGGER.error("Error retrieving conversation", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(false, "Server error", "errorServer", null))
                    .build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("all")
    public Response getAllUsersWhoChat(@CookieParam("jwt") String jwtToken) {
        LOGGER.info("Request to get all users who chat");

        // Validate JWT
        Long userId = JWTUtil.getUserIdFromToken(jwtToken);
        if (userId == null) {
            LOGGER.warn("Missing or invalid JWT in getAllUsersWhoChat");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ApiResponse(false, "Missing token", "errorMissingToken", null))
                    .build();
        }

        try {
            List<UserResponseDTO> userList = messageService.getAllChats(userId);
            if (userList == null || userList.isEmpty()) {
                LOGGER.info("No conversations found for userId {}", userId);
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ApiResponse(false, "No conversations found", "errorNoConversations", null))
                        .build();
            }

            LOGGER.info("All conversations retrieved for userId {}", userId);
            return Response.ok(new ApiResponse(true, "All conversations retrieved", "successConversationsRetrieved", userList))
                    .build();
        } catch (Exception e) {
            LOGGER.error("Error in getAllUsersWhoChat", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(false, "Internal server error", "errorInternal", null))
                    .build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendMessage(
            @CookieParam("jwt") String jwtToken,
            @Valid MessageSendDTO messageDTO) {

        LOGGER.info("Request to send message: {}", messageDTO);

        // Validate JWT
        Long senderId = JWTUtil.getUserIdFromToken(jwtToken);
        if (senderId == null) {
            LOGGER.warn("Unauthorized sendMessage request: missing or invalid JWT");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ApiResponse(false, "Unauthorized", "errorUnauthorized", null))
                    .build();
        }
        if(senderId == messageDTO.getReceiverId()) {
            LOGGER.warn("Sender and receiver cannot be the same user");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiResponse(false, "Sender and receiver cannot be the same user", "errorSameUser", null))
                    .build();
        }
        MessageDTO newMessage = new MessageDTO();
        newMessage.setSenderId(senderId);
        newMessage.setReceiverId(messageDTO.getReceiverId());
        newMessage.setMessageContent(messageDTO.getContent());
        newMessage.setSentDate(LocalDateTime.now());
        newMessage.setMessageIsRead(false);

        try {
            MessageDTO savedMessage = messageService.newMessage(newMessage);
            if (savedMessage == null) {
                LOGGER.error("Failed to save message from userId {} to userId {}", senderId, messageDTO.getReceiverId());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new ApiResponse(false, "Failed to send message", "errorSendMessage", null))
                        .build();
            }

            LOGGER.info("Message sent from userId {} to userId {}", senderId, messageDTO.getReceiverId());
            return Response.ok(new ApiResponse(true, "Message sent successfully", "successMessageSent", savedMessage))
                    .build();
        } catch (Exception e) {
            LOGGER.error("Exception in sendMessage", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(false, "Internal server error", "errorInternal", null))
                    .build();
        }
    }

    @PATCH
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response readConversation(
            @CookieParam("jwt") String jwtToken,
            @PathParam("id") Long senderId) {

        // Validate JWT
        Long recipientId = JWTUtil.getUserIdFromToken(jwtToken);
        if (recipientId == null) {
            LOGGER.warn("Unauthorized readConversation request: missing or invalid JWT");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ApiResponse(false, "Unauthorized", "errorUnauthorized", null))
                    .build();
        }

        if (senderId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiResponse(false, "Sender ID cannot be empty", "errorMissingSenderId", null))
                    .build();
        }

        try {
            boolean success = messageService.readAllConversation(recipientId, senderId);
            if (success) {
                LOGGER.info("Conversation read by userId {}", recipientId);
                return Response.ok(new ApiResponse(true, "Conversation read", "successConversationRead", null))
                        .build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ApiResponse(false, "No conversation found", "errorNoConversation", null))
                        .build();
            }
        } catch (Exception e) {
            LOGGER.error("Exception in readConversation", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(false, "Internal server error", "errorInternal", null))
                    .build();
        }
    }
}