package pt.uc.dei.proj5.service;

import jakarta.inject.Inject;
import jakarta.jms.Message;
import jakarta.json.JsonObject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.proj5.beans.MessageBean;
import pt.uc.dei.proj5.beans.NotificationBean;
import pt.uc.dei.proj5.beans.UserBean;
import pt.uc.dei.proj5.dto.*;
import pt.uc.dei.proj5.websocket.wsChat;

import java.time.LocalDateTime;
import java.util.List;

@Path("/messages")
public class MessageService {
    private static final Logger logger = LogManager.getLogger(MessageService.class);

    @Inject
    AuthenticationService authenticationService;

    @Inject
    UserBean userBean;

    @Inject
    MessageBean messageBean;

    @Inject
    NotificationBean notificationBean;

    @Inject
    wsChat wsChat;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{username}")
    public Response getChat(@HeaderParam("token") String authenticationToken,
                            @PathParam("username") String username) {
        UserDto user;
        try {
            user = authenticationService.validateAuthenticationToken(authenticationToken);
        } catch (WebApplicationException e) {
            return e.getResponse();
        }
        UserDto otherUser = userBean.getUserInformation(username);
        if (otherUser == null) {
            logger.error("Invalid other user - getting messages");
            return Response.status(404).entity("Other user doesn't exist").build();
        }
        if (otherUser.getState() == UserAccountState.INACTIVE || otherUser.getState() == UserAccountState.EXCLUDED) {
            logger.error("Invalid other user - getting messages");
            return Response.status(403).entity("Other user has inactive or excluded account").build();
        }
        List<MessageDto> conversation = messageBean.getMessagesBetween(user, otherUser);
        if (conversation == null) {
            logger.error("Invalid conversation - getting messages");
            return Response.status(404).entity("No conversation found").build();
        } else {
            logger.info("Conversation retrieved");
            return Response.status(200).entity(conversation).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("all")
    public Response getAllUsersWhoChat(@HeaderParam("token") String authenticationToken) {
        UserDto user;
        try {
            user = authenticationService.validateAuthenticationToken(authenticationToken);
        } catch (WebApplicationException e) {
            return e.getResponse();
        }
        List<UserDto> userList = messageBean.getAllChats(user);
        if (userList == null) {
            logger.error("Invalid conversation - getting messages");
            return Response.status(404).entity("No conversations found").build();
        } else {
            logger.info("All conversations retrieved");
            return Response.status(200).entity(userList).build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendMessage(@HeaderParam("token") String authenticationToken, JsonObject payload) {
        UserDto sender;
        String message = payload.getString("message");
        String recipient = payload.getString("recipient");

        if (recipient == null || recipient.isEmpty()) {
            return Response.status(400).entity("Recipient cannot be empty").build();
        }
        try {
            sender = authenticationService.validateAuthenticationToken(authenticationToken);
        } catch (WebApplicationException e) {
            return e.getResponse();
        }
        JsonObject messageJson = wsChat.archiveNewMessage(message, sender.getUsername(), recipient);
        if (wsChat.sendMessageToUser(messageJson, recipient)) {
            logger.info("Message sent to user " + sender.getUsername());
            return Response.status(200).entity(messageJson).build();
        } else {
            if(notificationBean.newMessageNotification(message, sender.getUsername(), recipient)) {
                logger.error("Message sucessfully sent via http " + recipient);
                return Response.status(200).entity("Message sucessfully sent via http " + recipient).build();
            }
            else {
                logger.error("Failed to message user " + recipient);
                return Response.status(403).entity("Failed to message user " + recipient).build();
            }
        }
    }

    @PATCH
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/conversation")
    public Response readConversation(@HeaderParam("token") String authenticationToken,
                                     @QueryParam("username") String sender) {
        UserDto recipient;
        if (sender == null || sender.isEmpty()) {
            return Response.status(400).entity("Recipient cannot be empty").build();
        }
        try {
            recipient = authenticationService.validateAuthenticationToken(authenticationToken);
        } catch (WebApplicationException e) {
            return e.getResponse();
        }
        if (messageBean.readAllConversation(recipient.getUsername(), sender)) {
            JsonObject readConversations = JsonCreator.createJson("CONVERSATION_READ", "sender", recipient.getUsername());
            wsChat.sendMessageToUser(readConversations, sender);
            logger.info("Conversation read");return Response.status(200).entity("Conversation read by " + recipient.getUsername()).build();
        }
        else {
            return Response.status(403).entity("No conversation found").build();
        }
    }
}