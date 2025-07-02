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
import org.jboss.resteasy.annotations.LinkHeaderParam;
import pt.uc.dei.dtos.NotificationDTO;
import pt.uc.dei.services.AuthenticationService;
import pt.uc.dei.services.NotificationService;
import pt.uc.dei.utils.JsonCreator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Set;

/**
 * Endpoint WebSocket responsável por gerenciar notificações enviadas a usuários autenticados.
 * Este endpoint lida com autenticação de usuários, envio de notificações,
 * envio de mensagens PING para manter as conexões ativas e encerramento de sessões.
 */
@Singleton
@ServerEndpoint(value = "/websocket/notifications/", configurator = CustomConfigurator.class)
public class WsNotifications {

    // Logger para registrar informações sobre conexões, erros ou eventos importantes
    private static final Logger logger = LogManager.getLogger(WsNotifications.class);

    // HashMap que armazena as sessões WebSocket de cada usuário autenticado.
    // A chave é o nome do usuário, e o valor é o conjunto de sessões do usuário.
    private HashMap<Long, Set<Session>> sessions = new HashMap<>();

    @Inject
    private WebSocketAuthentication webSocketAuthentication;

    @Inject
    private AuthenticationService authenticationService;

    @Inject
    private NotificationService notificationService;

    /**
     * Método chamado automaticamente quando uma nova conexão WebSocket é estabelecida.
     * Este método tenta autenticar o usuário com base na solicitação de handshake (JWT nos cookies ou cabeçalhos)
     * e registra a sessão do usuário se a autenticação for bem-sucedida.
     *
     * @param session A nova sessão WebSocket estabelecida.
     * @param config  Configurações do endpoint.
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        try {
            // Tenta autenticar usando a solicitação de handshake (JWT nos cookies ou cabeçalhos)
            HandshakeRequest request = (HandshakeRequest) config.getUserProperties().get("HandshakeRequest");
            boolean authenticated = webSocketAuthentication.authenticate(session, request, sessions);
            if (!authenticated) {
                logger.warn("WebSocket authentication failed: missing or invalid JWT/token");
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Unauthorized: missing or invalid JWT/token"));
                return;
            }
            // Após a autenticação bem-sucedida, envia a contagem de notificações para o usuário
            Long userId = webSocketAuthentication.findUserIdBySession(sessions, session);
            if (userId != null && notificationService != null) {
                int notificationsCount = notificationService.getTotalNotifications(userId);
                JsonObject notificationsJSON = Json.createObjectBuilder()
                        .add("type", "NOTIFICATION_COUNT")
                        .add("count", notificationsCount)
                        .build();
                session.getBasicRemote().sendText(notificationsJSON.toString());
            }
        } catch (Exception e) {
            logger.error("WebSocket authentication error", e);
            try { session.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, "Authentication error")); } catch (Exception ignore) {}
        }
    }


    /**
     * Método chamado automaticamente quando uma conexão WebSocket é fechada.
     * Remove a sessão do cliente da lista de sessões ativas e, se o usuário não tiver mais
     * sessões ativas, remove completamente o usuário.
     *
     * @param session A sessão WebSocket que foi fechada.
     * @param reason  O motivo do fechamento da conexão.
     */
    @OnClose
    public void onClose(Session session, CloseReason reason) {
        Long userId = WebSocketAuthentication.findUserIdBySession(sessions, session);
        authenticationService.setUserOffline(userId);
        if (userId != null) {
            Set<Session> userSessions = sessions.get(userId);
            if (userSessions != null) {
                userSessions.remove(session); // Remove apenas a sessão fechada
                if (userSessions.isEmpty()) {
                    sessions.remove(userId); // Remove o usuário se não tiver mais sessões
                }
            }
            logger.info("User {} disconnected from notifications. Reason: {}", userId, reason.getReasonPhrase());
        } else {
            logger.info("Unknown WebSocket session closed: {}", reason.getReasonPhrase());
        }
    }

    /**
     * Método chamado automaticamente quando o servidor recebe uma mensagem de um cliente.
     * Este método trata mensagens relacionadas à autenticação e envia ao usuário, caso autenticado,
     * a contagem de notificações pendentes.
     *
     * @param session A sessão WebSocket de onde veio a mensagem.
     * @param msg     A mensagem enviada pelo cliente (em formato JSON).
     * @throws IOException Se ocorrer erro ao enviar resposta para o cliente.
     */
    @OnMessage
    public void toDoOnMessage(Session session, String msg) throws IOException {
        JsonReader jsonReader = Json.createReader(new StringReader(msg));
        JsonObject jsonMessage = jsonReader.readObject();
        String messageType = jsonMessage.getString("type");

        switch (messageType) {
            default: // Se o tipo de mensagem não for reconhecido
                logger.info("Received unknown message type: " + messageType);
        }
    }

    /**
     * Envia uma notificação para todas as sessões pertencentes a um usuário.
     *
     * @param notificationDto Mensagem contendo os dados da notificação.
     * @return Retorna `true` se a notificação foi enviada com sucesso para pelo menos uma sessão; caso contrário, `false`.
     * @throws Exception Se ocorrer um erro ao criar ou enviar a notificação.
     */
    public boolean notifyUser(NotificationDTO notificationDto) throws Exception {
        try {
            // Cria o JSON da notificação usando um utilitário
            JsonObject notificationsJson = JsonCreator.createJson(notificationDto.getType().toString().toUpperCase(), "notification", notificationDto);
            String notificationJsonString = notificationsJson.toString();

            // Envia a notificação para as sessões do usuário
            if (sendNotificationToUserSessions(notificationDto.getId(), notificationJsonString)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            logger.error("Error while creating JSON object", e);
            return false;
        }
    }

    /**
     * Envia uma notificação para todas as sessões abertas de um usuário específico.
     *
     * @param recipientId O nome do usuário destinatário da notificação.
     * @param notification       A notificação que será enviada (em formato JSON String).
     * @return Retorna `true` se a notificação foi enviada com sucesso; caso contrário `false`.
     * @throws Exception Se ocorrer erro ao enviar a notificação.
     */
    private boolean sendNotificationToUserSessions(Long recipientId, String notification) throws Exception {
        try {
            Set<Session> recipientSessions = sessions.get(recipientId);
            if (recipientSessions != null && !recipientSessions.isEmpty()) {
                for (Session session : recipientSessions) {
                    // Double-check: session must still be mapped to this userId
                    Long mappedUserId = WebSocketAuthentication.findUserIdBySession(sessions, session);
                    if (session.isOpen() && recipientId.equals(mappedUserId)) {
                        session.getBasicRemote().sendText(notification);
                    }
                }
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            logger.error("Failed to send notification to user {}", recipientId, e);
            return false;
        }
    }

    /**
     * Método chamado automaticamente quando o servidor recebe uma mensagem PONG de uma sessão.
     * Essa mensagem é usada para verificar se a conexão do cliente está ativa.
     *
     * @param session     A sessão WebSocket que enviou a mensagem PONG.
     * @param pongMessage A mensagem PONG enviada pelo cliente.
     */
    @OnMessage
    public void handlePing(Session session, PongMessage pongMessage) {
        logger.info("Received WebSocket PONG from session {}: {}", session.getId(), pongMessage);
    }

    /**
     * Envia mensagens do tipo PING para todas as conexões WebSocket ativas a cada 60 segundos.
     * O envio de PING serve para manter as conexões ativas e identificar sessões inativas.
     */
    @Schedule(second = "*/60", minute = "*", hour = "*") // Executado automaticamente a cada 60 segundos
    private void pingUsers() {
        for (Set<Session> userSessions : sessions.values()) { // Itera sobre os conjuntos de sessões dos usuários
            for (Session session : userSessions) { // Itera sobre as sessões individuais
                if (session.isOpen()) { // Verifica se a sessão está aberta
                    try {
                        session.getBasicRemote().sendPing(ByteBuffer.wrap(new byte[0])); // Envia o PING
                    } catch (IOException e) {
                        logger.error("Failed to send WebSocket PING to session {}", session.getId(), e);
                    }
                }
            }
        }
    }

    
}