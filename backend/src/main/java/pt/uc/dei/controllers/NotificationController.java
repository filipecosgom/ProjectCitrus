package pt.uc.dei.controllers;

import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pt.uc.dei.dtos.NotificationDTO;
import pt.uc.dei.services.NotificationService;
import pt.uc.dei.utils.JWTUtil;

import java.util.List;

@Path("/notifications")
public class NotificationController {
    private static final Logger LOGGER = LogManager.getLogger(NotificationController.class);

    NotificationService notificationService = new NotificationService(); // Assuming a default constructor is available

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNotifications(@Context ContainerRequestContext requestContext) {
        Long userId = JWTUtil.getIdFromContainerRequestContext(requestContext);
        if (userId == null) {
            LOGGER.warn("Unauthorized getNotifications request: missing or invalid JWT");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new pt.uc.dei.utils.ApiResponse(false, "Unauthorized", "errorUnauthorized", null))
                    .build();
        }
        try {
            List<NotificationDTO> notificationDtos = notificationService.getNotifications(userId);
            if (notificationDtos == null || notificationDtos.isEmpty()) {
                LOGGER.info("No notifications found for userId {}", userId);
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new pt.uc.dei.utils.ApiResponse(false, "No notifications found", "errorNoNotifications", null))
                        .build();
            } else {
                LOGGER.info("User {} got notifications", userId);
                return Response.ok(new pt.uc.dei.utils.ApiResponse(true, "Notifications retrieved", "successNotificationsRetrieved", notificationDtos)).build();
            }
        } catch (Exception e) {
            LOGGER.error("Exception in getNotifications", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new pt.uc.dei.utils.ApiResponse(false, "Internal server error", "errorInternal", null))
                    .build();
        }
    }

    @PATCH
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/read/{notificationId}")
    public Response readNotification(@Context ContainerRequestContext requestContext,
                                     @PathParam("notificationId") Long notificationId) {
        Long userId = JWTUtil.getIdFromContainerRequestContext(requestContext);
        if (userId == null) {
            LOGGER.warn("Unauthorized readNotification request: missing or invalid JWT");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new pt.uc.dei.utils.ApiResponse(false, "Unauthorized", "errorUnauthorized", null))
                    .build();
        }
        if (notificationId == null) {
            LOGGER.error("Invalid notification id - reading notification");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new pt.uc.dei.utils.ApiResponse(false, "Invalid notification id", "errorInvalidNotificationId", null))
                    .build();
        }
        try {
            boolean read = notificationService.readNotification(notificationId, userId);
            if (!read) {
                LOGGER.error("Notification {} does not exist or could not be marked as read for userId {}", notificationId, userId);
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new pt.uc.dei.utils.ApiResponse(false, "Notification does not exist or could not be marked as read", "errorNotificationNotFoundOrUnreadable", null))
                        .build();
            }
            LOGGER.info("User {} read notification {}", userId, notificationId);
            return Response.ok(new pt.uc.dei.utils.ApiResponse(true, "Notification marked as read", "successNotificationRead", notificationId)).build();
        } catch (Exception e) {
            LOGGER.error("Exception in readNotification", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new pt.uc.dei.utils.ApiResponse(false, "Internal server error", "errorInternal", null))
                    .build();
        }
    }
}