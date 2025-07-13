package pt.uc.dei.controllers;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.dtos.NotificationDTO;
import pt.uc.dei.dtos.NotificationUpdateDTO;
import pt.uc.dei.services.NotificationService;
import pt.uc.dei.utils.JWTUtil;
import pt.uc.dei.utils.ApiResponse;

import java.util.List;

@Stateless
@Path("/notifications")
public class NotificationController {
    private static final Logger LOGGER = LogManager.getLogger(NotificationController.class);
    
    @Inject
    private NotificationService notificationService;

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
                return Response.ok()
                        .entity(new ApiResponse(true, "Notifications fetched", "notificationsFetched", notificationDtos))
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
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateNotificationStatus(
            @CookieParam("jwt") String jwtToken,
            @Valid NotificationUpdateDTO updateDTO) {

        // Validate JWT
        Long userId = JWTUtil.getUserIdFromToken(jwtToken);
        if (userId == null) {
            LOGGER.warn("Unauthorized updateNotificationStatus request: missing or invalid JWT");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ApiResponse(false, "Unauthorized", "errorUnauthorized", null))
                    .build();
        }

        if (updateDTO == null || updateDTO.getNotificationId() == null) {
            LOGGER.error("Invalid NotificationUpdateDTO or notificationId");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiResponse(false, "Invalid notification update data", "errorInvalidNotificationUpdate", null))
                    .build();
        }

        try {
            boolean updated = notificationService.updateNotificationStatus(updateDTO, userId);
            if (!updated) {
                LOGGER.error("Notification {} could not be updated for userId {}", updateDTO.getNotificationId(), userId);
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ApiResponse(false, "Notification could not be updated",
                                "errorNotificationNotFoundOrUnupdatable", null))
                        .build();
            }

            LOGGER.info("User {} updated notification {} (isRead={}, isSeen={})", userId, updateDTO.getNotificationId(), updateDTO.getNotificationIsRead(), updateDTO.getNotificationIsSeen());
            return Response.ok(new ApiResponse(true, "Notification updated", "successNotificationUpdated", updateDTO.getNotificationId()))
                    .build();

        } catch (Exception e) {
            LOGGER.error("Exception in updateNotificationStatus", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(false, "Internal server error", "errorInternal", null))
                    .build();
        }
    }

    @PUT
    @Path("/mark-messages-read")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response markMessageNotificationsAsRead(@CookieParam("jwt") String jwtToken) {
        try {
            // ✅ USAR MESMO PADRÃO DOS OUTROS MÉTODOS
            Long userId = JWTUtil.getUserIdFromToken(jwtToken);
            if (userId == null) {
                LOGGER.warn("Unauthorized markMessageNotificationsAsRead request: missing or invalid JWT");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ApiResponse(false, "Unauthorized", "errorUnauthorized", null))
                        .build();
            }

            // ✅ MARCAR TODAS AS NOTIFICAÇÕES MESSAGE COMO LIDAS
            boolean success = notificationService.markMessageNotificationsAsRead(userId);

            if (success) {
                LOGGER.info("User {} marked all MESSAGE notifications as read", userId);
                return Response.ok(new ApiResponse(true, "Message notifications marked as read", "successNotificationsMarkedRead", null))
                        .build();
            } else {
                LOGGER.error("Failed to mark MESSAGE notifications as read for user {}", userId);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new ApiResponse(false, "Failed to mark notifications as read", "errorMarkingNotifications", null))
                        .build();
            }
        } catch (Exception e) {
            LOGGER.error("Error marking message notifications as read", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse(false, "Internal server error", "errorInternal", null))
                    .build();
        }
    }
}