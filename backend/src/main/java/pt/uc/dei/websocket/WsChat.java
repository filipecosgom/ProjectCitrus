package pt.uc.dei.websocket;

import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.websocket.*;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.dtos.MessageDTO;
import pt.uc.dei.dtos.UserDTO;
import pt.uc.dei.services.AuthenticationService;
import pt.uc.dei.services.MessageService;
import pt.uc.dei.services.NotificationService;
import pt.uc.dei.services.UserService;
import pt.uc.dei.utils.JsonCreator;

import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Set;

/**
 * Endpoint WebSocket responsável pela comunicação em tempo real no chat.
 * Lida com mensagens de autenticação, envio de mensagens, notificações,
 * recebimento de pings e gerenciamento de sessões de usuários.
 */
@Singleton
@ServerEndpoint(value = "/websocket/chat/", configurator = CustomConfigurator.class)
public class WsChat {

    // Logger usado para registrar eventos do WebSocket (ex.: conexões, mensagens,
    // erros)
    private static final Logger logger = LogManager.getLogger(WsChat.class);

    // Mapa que armazena as sessões dos usuários. A chave é o ID do usuário,
    // e o valor é um conjunto de sessões WebSocket associados a ele.
    private HashMap<Long, Set<Session>> sessions = new HashMap<>();

    @Inject
    private WebSocketAuthentication webSocketAuthentication;
    @Inject
    private AuthenticationService authenticationService;
    @Inject
    private UserService userService;
    @Inject
    private MessageService messageService;
    @Inject
    private NotificationService notificationService;

    /**
     * Método chamado automaticamente quando uma nova conexão WebSocket é
     * estabelecida.
     * Extrai o JWT dos cookies, autentica o usuário e registra a sessão.
     *
     * @param session A nova sessão WebSocket.
     * @param config  A configuração do endpoint.
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        try {
            HandshakeRequest request = (HandshakeRequest) config.getUserProperties().get("HandshakeRequest");
            boolean authenticated = webSocketAuthentication.authenticate(session, request, sessions);
            if (!authenticated) {
                logger.warn("WebSocket authentication failed: missing or invalid JWT cookie");
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY,
                        "Unauthorized: missing or invalid JWT"));
            }
        } catch (Exception e) {
            logger.error("WebSocket authentication error", e);
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, "Authentication error"));
            } catch (Exception ignore) {
            }
        }
    }

    /**
     * Método chamado automaticamente quando uma conexão WebSocket é fechada.
     * Remove a sessão WebSocket correspondente da lista de sessões ativas.
     * Caso o usuário não possua mais sessões abertas, ele é removido do mapa.
     *
     * @param session A sessão WebSocket que foi fechada.
     * @param reason  O motivo do fechamento.
     */
    @OnClose
    public void onClose(Session session, CloseReason reason) {
        Long userId = WebSocketAuthentication.findUserIdBySession(sessions, session);
        if (userId != null) {
            Set<Session> userSessions = sessions.get(userId);
            if (userSessions != null) {
                userSessions.remove(session); // Remove apenas a sessão encerrada
                if (userSessions.isEmpty()) {
                    sessions.remove(userId); // Remove o usuário se não houver mais sessões
                }
            }
            logger.info("User {} disconnected from chat. Reason: {}", userId, reason.getReasonPhrase());
        } else {
            logger.info("Unknown WebSocket session closed: {}", reason.getReasonPhrase());
        }
    }

    /**
     * Método chamado automaticamente quando uma mensagem é recebida pelo WebSocket.
     * Este método processa tipos específicos de mensagens, como autenticação e
     * envio de mensagens.
     *
     * @param session A sessão WebSocket de onde veio a mensagem.
     * @param msg     A mensagem enviada pelo cliente, no formato JSON.
     * @throws IOException Se houver erro ao enviar uma resposta para o cliente.
     */
    @OnMessage
    public void toDoOnMessage(Session session, String msg) throws IOException {
        System.out.println("Received message: " + msg);
        JsonReader jsonReader = Json.createReader(new StringReader(msg));
        JsonObject jsonMessage = jsonReader.readObject();
        String messageType = jsonMessage.getString("type");

        switch (messageType) {
            case "MESSAGE": {
                if (!checkIfValidMessage(jsonMessage)) {
                    session.getBasicRemote().sendText(
                            JsonCreator.createJson("ERROR", "message", "Invalid message format").toString());
                } else {
                    Long recipientId = jsonMessage.getJsonNumber("recipientId").longValue();
                    String message = jsonMessage.getString("message").trim();
                    Long senderId = WebSocketAuthentication.findUserIdBySession(sessions, session);
                    UserDTO recipientUser = userService.getUser(recipientId);
                    if (recipientUser != null) {
                        if (recipientUser.getUserIsDeleted()) {
                            logger.info("Recipient {} is deleted", recipientUser.getId());
                            JsonObject errorJson = JsonCreator.createJson("ERROR", "message",
                                    "Recipient user is deleted");
                            session.getBasicRemote().sendText(errorJson.toString());
                            return;
                        }
                        MessageDTO messageDTO = new MessageDTO();
                        messageDTO.setSenderId(senderId);
                        messageDTO.setRecipientId(recipientId);
                        messageDTO.setContent(message);
                        messageDTO.setSentDate(LocalDateTime.now());
                        messageDTO.setMessageIsRead(false);
                        if (messageDTO != null) {
                            boolean delivered = sendMessageToUser(messageDTO);
                            if (delivered) {
                                messageDTO.setMessageIsRead(true);
                                messageDTO = messageService.archiveMessage(messageDTO);
                                JsonObject confirmationJson = JsonCreator.createJson("SUCCESS", "message",
                                        "Message sent successfully");
                                session.getBasicRemote().sendText(confirmationJson.toString());
                            } else {
                                messageDTO = messageService.archiveMessage(messageDTO);
                                notificationService.newMessageNotification(messageDTO);
                            }
                        } else {
                            session.getBasicRemote().sendText(
                                    JsonCreator.createJson("ERROR", "message", "Failed to archive message").toString());
                        }
                    } else {
                        logger.info("Recipient {} does not exist", recipientId);
                        JsonObject errorJson = JsonCreator.createJson("ERROR", "message",
                                "Recipient user does not exist");
                        session.getBasicRemote().sendText(errorJson.toString());
                    }
                }
                break;
            }
            case "CONVERSATION_READ": {
                Long recipientId = jsonMessage.getJsonNumber("recipientId").longValue();
                Long senderId = WebSocketAuthentication.findUserIdBySession(sessions, session);
                JsonObject conversationRead = JsonCreator.createJson("CONVERSATION_READ", "senderId", senderId);
                sendJsonToUser(conversationRead, recipientId);
                break;
            }
            default:
                logger.info("Received unknown message type: {}", messageType);
                break;
        }
    }

    /**
     * Envia uma mensagem para todas as sessões associadas ao usuário destinatário.
     *
     * @param messageDTO O DTO da mensagem a ser enviada.
     * @return `true` se pelo menos uma sessão recebeu a mensagem; caso contrário,
     *         `false`.
     */
    public boolean sendMessageToUser(MessageDTO messageDTO) {
        Long recipientUserId = messageDTO.getRecipientId();
        Set<Session> recipientSessions = sessions.get(recipientUserId);
        if (recipientSessions != null) {
            JsonObject messageJson = JsonCreator.createJson("MESSAGE", "message", messageDTO);
            for (Session recipientSession : recipientSessions) {
                if (recipientSession.isOpen()) {
                    try {
                        recipientSession.getBasicRemote().sendText(messageJson.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Envia um objeto JSON para todas as sessões associadas ao usuário
     * destinatário.
     *
     * @param json            O objeto JSON a ser enviado.
     * @param recipientUserId O ID do usuário destinatário.
     * @return `true` se pelo menos uma sessão recebeu a mensagem; caso contrário,
     *         `false`.
     */
    public boolean sendJsonToUser(JsonObject json, Long recipientUserId) {
        System.out.println("Sending JSON to user: " + recipientUserId + ", JSON: " + json);
        Set<Session> recipientSessions = sessions.get(recipientUserId);
        if (recipientSessions != null) {
            for (Session recipientSession : recipientSessions) {
                if (recipientSession.isOpen()) {
                    try {
                        recipientSession.getBasicRemote().sendText(json.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Verifica se a mensagem recebida é válida.
     *
     * @param jsonMessage O objeto JSON da mensagem.
     * @return `true` se a mensagem for válida; caso contrário, `false`.
     */
    private boolean checkIfValidMessage(JsonObject jsonMessage) {
        System.out.println("Checking if message is valid: " + jsonMessage);
        return jsonMessage.containsKey("recipientId") &&
                jsonMessage.containsKey("message") &&
                jsonMessage.get("recipientId") != null &&
                jsonMessage.get("message") != null &&
                !jsonMessage.getJsonNumber("recipientId").toString().trim().isEmpty() &&
                !jsonMessage.getString("message").trim().isEmpty();
    }

    /**
     * Método chamado automaticamente quando o servidor recebe uma mensagem PONG.
     *
     * @param session     A sessão que enviou o PONG.
     * @param pongMessage A mensagem PONG recebida.
     */
    @OnMessage
    public void handlePing(Session session, PongMessage pongMessage) {
        logger.info("Received WebSocket PONG from session {}: {}", session.getId(), pongMessage);
    }

    /**
     * Envia mensagens do tipo PING para todas as sessões ativas a cada 60 segundos.
     */
    @Schedule(second = "*/60", minute = "*", hour = "*") // Executa a cada 60 segundos
    private void pingUsers() {
        for (Set<Session> userSessions : sessions.values()) { // Itera sobre os conjuntos de sessões
            for (Session session : userSessions) { // Itera sobre sessões individuais
                if (session.isOpen()) {
                    try {
                        session.getBasicRemote().sendPing(ByteBuffer.wrap(new byte[0])); // Envia um PING
                    } catch (IOException e) {
                        logger.error("Failed to send WebSocket PING to session {}", session.getId(), e);
                    }
                }
            }
        }
    }
}