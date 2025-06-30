package pt.uc.dei.proj5.service;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.proj5.beans.NotificationBean;
import pt.uc.dei.proj5.dto.*;

import java.util.List;

@Path("/notifications")
public class NotificationService {
    private static final Logger logger = LogManager.getLogger(NotificationService.class);

    @Inject
    AuthenticationService authenticationService;

    @Inject
    NotificationBean notificationBean;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNotifications(@HeaderParam("token") String authenticationToken) {
        UserDto user;
        try {
            user = authenticationService.validateAuthenticationToken(authenticationToken);
        } catch (WebApplicationException e) {
            return e.getResponse();
        }
        List<NotificationDto> notificationDtos = notificationBean.getNotifications(user);
            if (notificationDtos == null) {
                logger.error("No notifications - getting notifications - user {} tried get notifications", user.getUsername());
                return Response.status(404).entity("No notifications - getting notifications").build();
            } else {
                logger.info("User {} got notifications", user.getUsername());
                return Response.status(200).entity(notificationDtos).build();
            }
        }

    @PATCH
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/read/{notificationId}")
    public Response readNotification(
            @HeaderParam("token") String authenticationToken,
            @PathParam("notificationId") Long notificationId) {
        UserDto user;
        try {
            user = authenticationService.validateAuthenticationToken(authenticationToken);
        } catch (WebApplicationException e) {
            return e.getResponse();
        }
        if(notificationId == null) {
            logger.error("Invalid notification id - getting notifications");
            return Response.status(401).entity("Invalid notification id").build();
        }
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setId(notificationId);
        if(!notificationBean.checkIfNotificationExists(notificationDto, user)) {
            logger.error("Notification {} does not exist", notificationId);
            return Response.status(404).entity("Notification " + notificationId + " does not exist").build();
        }
        else {
            if(!notificationBean.readNotification(notificationDto, user)){
                logger.error("Reading {} failed", notificationId);
                return Response.status(500).entity("Reading " + notificationId + " failed").build();
            }
            else {
                logger.info("User {} read notification {}", user.getUsername(), notificationId);
                return Response.status(200).entity(notificationDto).build();
            }
        }
    }
}