package pt.uc.dei.proj5.websocket;

import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.proj5.beans.NotificationBean;
import pt.uc.dei.proj5.beans.TokenBean;
import pt.uc.dei.proj5.dto.*;

import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Endpoint WebSocket responsável por gerenciar notificações enviadas a usuários autenticados.
 * Este endpoint lida com autenticação de usuários, envio de notificações,
 * envio de mensagens PING para manter as conexões ativas e encerramento de sessões.
 */
@Singleton
@ServerEndpoint("/websocket/notifications/")
public class wsNotifications {

    // Logger para registrar informações sobre conexões, erros ou eventos importantes
    private static final Logger logger = LogManager.getLogger(wsNotifications.class);

    // HashMap que armazena as sessões WebSocket de cada usuário autenticado.
    // A chave é o nome do usuário, e o valor é o conjunto de sessões do usuário.
    private HashMap<String, Set<Session>> sessions = new HashMap<>();

    // Bean responsável por gerenciar tokens para autenticação
    @Inject
    TokenBean tokenBean;

    // Bean responsável por operações relacionadas a notificações (ex: obter contagem)
    @Inject
    NotificationBean notificationBean;

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
        String username = WebSocketAuthentication.findUsernameBySession(sessions, session);

        if (username != null) {
            Set<Session> userSessions = sessions.get(username);
            if (userSessions != null) {
                userSessions.remove(session); // Remove apenas a sessão fechada
                if (userSessions.isEmpty()) {
                    sessions.remove(username); // Remove o usuário se não tiver mais sessões
                }
            }
            logger.info("User {} disconnected from chat. Reason: {}", username, reason.getReasonPhrase());
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
            case "AUTHENTICATION": { // Se a mensagem for do tipo "AUTENTICAÇÃO"
                if (WebSocketAuthentication.authenticate(session, jsonMessage, tokenBean, sessions)) {
                    UserDto user = new UserDto();
                    user.setUsername(WebSocketAuthentication.findUsernameBySession(sessions, session));
                    int notificationsCount = notificationBean.getTotalNotifications(user);

                    // Envia ao cliente a contagem de notificações como resposta
                    JsonObject notificationsJSON = Json.createObjectBuilder()
                            .add("type", "NOTIFICATION_COUNT")
                            .add("count", notificationsCount)
                            .build();
                    session.getBasicRemote().sendText(notificationsJSON.toString());
                }
                break;
            }
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
    public boolean notifyUser(NotificationDto notificationDto) throws Exception {
        try {
            // Cria o JSON da notificação usando um utilitário
            JsonObject notificationsJson = JsonCreator.createJson(notificationDto.getType().toString().toUpperCase(), "notification", notificationDto);
            String notificationJsonString = notificationsJson.toString();

            // Envia a notificação para as sessões do usuário
            if (sendNotificationToUserSessions(notificationDto.getRecipientUsername(), notificationJsonString)) {
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
     * @param recipientUsername O nome do usuário destinatário da notificação.
     * @param notification       A notificação que será enviada (em formato JSON String).
     * @return Retorna `true` se a notificação foi enviada com sucesso; caso contrário `false`.
     * @throws Exception Se ocorrer erro ao enviar a notificação.
     */
    private boolean sendNotificationToUserSessions(String recipientUsername, String notification) throws Exception {
        try {
            Set<Session> recipientSessions = sessions.get(recipientUsername);
            if (recipientSessions != null && !recipientSessions.isEmpty()) {
                for (Session session : recipientSessions) {
                    if (session.isOpen()) {
                        session.getBasicRemote().sendText(notification); // Envia a notificação
                    }
                }
                return true;
            } else {
                return false; // Nenhuma sessão para o usuário
            }

        } catch (IOException e) {
            logger.error("Failed to send notification to user {}", recipientUsername, e);
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