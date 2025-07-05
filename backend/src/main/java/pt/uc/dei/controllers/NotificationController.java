package pt.uc.dei.controllers;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.dtos.NotificationDTO;
import pt.uc.dei.services.NotificationService;
import pt.uc.dei.utils.JWTUtil;
import pt.uc.dei.utils.ApiResponse;

import java.util.List;

@Path("/notifications")
public class NotificationController {
    private static final Logger LOGGER = LogManager.getLogger(NotificationController.class);
    private final NotificationService notificationService = new NotificationService();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNotifications(@CookieParam("jwt") String jwtToken) {
        // Validate JWT
        Long userId = JWTUtil.getUserIdFromToken(jwtToken);
        if (userId == null) {
            LOGGER.warn("Unauthorized getNotifications request: missing or invalid JWT");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ApiResponse(false, "Unauthorized", "errorUnauthorized", null))
                    .build();
        }

        try {
            List<NotificationDTO> notificationDtos = notificationService.getNotifications(userId);
            if (notificationDtos == null || notificationDtos.isEmpty()) {
                LOGGER.info("No notifications found for userId {}", userId);
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ApiResponse(false, "No notifications found", "errorNoNotifications", null))
                        .build();
            }

            LOGGER.info("User {} got notifications", userId);
            return Response.ok(new ApiResponse(true, "Notifications retrieved", "successNotificationsRetrieved", notificationDtos))
                    .build();

        } catch (Exception e) {
            LOGGER.error("Exception in getNotifications", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(false, "Internal server error", "errorInternal", null))
                    .build();
        }
    }

    @PATCH
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/read/{notificationId}")
    public Response readNotification(
            @CookieParam("jwt") String jwtToken,
            @PathParam("notificationId") Long notificationId) {

        // Validate JWT
        Long userId = JWTUtil.getUserIdFromToken(jwtToken);
        if (userId == null) {
            LOGGER.warn("Unauthorized readNotification request: missing or invalid JWT");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ApiResponse(false, "Unauthorized", "errorUnauthorized", null))
                    .build();
        }

        // Validate notificationId
        if (notificationId == null) {
            LOGGER.error("Invalid notification id - reading notification");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiResponse(false, "Invalid notification id", "errorInvalidNotificationId", null))
                    .build();
        }

        try {
            boolean read = notificationService.readNotification(notificationId, userId);
            if (!read) {
                LOGGER.error("Notification {} does not exist or could not be marked as read for userId {}", notificationId, userId);
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ApiResponse(false, "Notification does not exist or could not be marked as read",
                                "errorNotificationNotFoundOrUnreadable", null))
                        .build();
            }

            LOGGER.info("User {} read notification {}", userId, notificationId);
            return Response.ok(new ApiResponse(true, "Notification marked as read", "successNotificationRead", notificationId))
                    .build();

        } catch (Exception e) {
            LOGGER.error("Exception in readNotification", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(false, "Internal server error", "errorInternal", null))
                    .build();
        }
    }

    @PUT
    @Path("/mark-messages-read")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response markMessageNotificationsAsRead(@CookieParam("session") String sessionId) {
        try {
            if (sessionId == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ResponseMessageDTO("Unauthorized", "Session not found"))
                        .build();
            }

            UserDTO user = authenticationService.getUserBySessionId(sessionId);
            if (user == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ResponseMessageDTO("Unauthorized", "Invalid session"))
                        .build();
            }

            // ✅ MARCAR TODAS AS NOTIFICAÇÕES MESSAGE COMO LIDAS
            boolean success = notificationService.markMessageNotificationsAsRead(user.getId());

            if (success) {
                return Response.ok(new ResponseMessageDTO("Success", "Message notifications marked as read"))
                        .build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new ResponseMessageDTO("Error", "Failed to mark notifications as read"))
                        .build();
            }
        } catch (Exception e) {
            logger.error("Error marking message notifications as read", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ResponseMessageDTO("Error", "Internal server error"))
                    .build();
        }
    }
}