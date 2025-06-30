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
import pt.uc.dei.proj5.beans.MessageBean;
import pt.uc.dei.proj5.beans.NotificationBean;
import pt.uc.dei.proj5.beans.TokenBean;
import pt.uc.dei.proj5.beans.UserBean;
import pt.uc.dei.proj5.dto.*;

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
@ServerEndpoint("/websocket/chat/")
public class wsChat {

    // Logger usado para registrar eventos do WebSocket (ex.: conexões, mensagens, erros)
    private static final Logger logger = LogManager.getLogger(wsChat.class);

    // Mapa que armazena as sessões dos usuários. A chave é o nome de usuário,
    // e o valor é um conjunto de sessões WebSocket associados a ele.
    private HashMap<String, Set<Session>> sessions = new HashMap<>();

    // Beans necessários para autenticação, envio de notificações,
    // manipulação de mensagens e verificação de usuários.
    @Inject
    TokenBean tokenBean;
    @Inject
    MessageBean messageBean;
    @Inject
    NotificationBean notificationBean;
    @Inject
    UserBean userBean;

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
        String username = WebSocketAuthentication.findUsernameBySession(sessions, session);
        if (username != null) {
            Set<Session> userSessions = sessions.get(username);
            if (userSessions != null) {
                userSessions.remove(session); // Remove apenas a sessão encerrada
                if (userSessions.isEmpty()) {
                    sessions.remove(username); // Remove o usuário se não houver mais sessões
                }
            }
            logger.info("User {} disconnected from chat. Reason: {}", username, reason.getReasonPhrase());
        } else {
            logger.info("Unknown WebSocket session closed: {}", reason.getReasonPhrase());
        }
    }

    /**
     * Método chamado automaticamente quando uma mensagem é recebida pelo WebSocket.
     * Este método processa tipos específicos de mensagens, como autenticação e envio de mensagens.
     *
     * @param session A sessão WebSocket de onde veio a mensagem.
     * @param msg     A mensagem enviada pelo cliente, no formato JSON.
     * @throws IOException Se houver erro ao enviar uma resposta para o cliente.
     */
    @OnMessage
    public void toDoOnMessage(Session session, String msg) throws IOException {
        JsonReader jsonReader = Json.createReader(new StringReader(msg));
        JsonObject jsonMessage = jsonReader.readObject();
        String messageType = jsonMessage.getString("type");

        switch (messageType) {
            case "AUTHENTICATION": { // Processa uma mensagem de autenticação
                WebSocketAuthentication.authenticate(session, jsonMessage, tokenBean, sessions);
                break;
            }
            case "MESSAGE": { // Processa uma mensagem de chat
                System.out.println(msg);
                if (!checkIfValidMessage(jsonMessage)) { // Verifica se a mensagem é válida
                    session.getBasicRemote().sendText(
                            JsonCreator.createJson("ERROR", "message", "Invalid message format").toString());
                } else {
                    String recipient = jsonMessage.getString("recipient").trim();
                    String message = jsonMessage.getString("message").trim();
                    String sender = WebSocketAuthentication.findUsernameBySession(sessions, session);

                    // Verifica se o destinatário existe no sistema
                    if (userBean.checkIfUserExists(recipient)) {
                        JsonObject messageJson = archiveNewMessage(message, sender, recipient);

                        // Tenta enviar a mensagem diretamente para o destinatário
                        if (sendMessageToUser(messageJson, recipient)) {
                            JsonObject confirmationJson = JsonCreator.createJson("SUCCESS", "message", "Message sent successfully");
                            session.getBasicRemote().sendText(confirmationJson.toString());
                        } else {
                            // Caso o destinatário não esteja conectado, cria uma notificação
                            notificationBean.newMessageNotification(message, sender, recipient);
                        }
                    } else {
                        logger.info("Recipient {} does not exist", recipient);
                        JsonObject errorJson = JsonCreator.createJson("ERROR", "message", "Recipient user does not exist");
                        session.getBasicRemote().sendText(errorJson.toString());
                    }
                }
                break;
            }
            case "CONVERSATION_READ": { // Marca uma conversa como lida
                String sender = jsonMessage.getString("sender").trim();
                String recipient = session.getId();
                JsonObject conversationRead = JsonCreator.createJson("CONVERSATION_READ", "sender", sender);
                sendMessageToUser(conversationRead, recipient);
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
     * @param messageJson       O objeto JSON que contém os dados da mensagem.
     * @param recipientUsername O nome do usuário destinatário.
     * @return `true` se pelo menos uma sessão recebeu a mensagem; caso contrário, `false`.
     */
    public boolean sendMessageToUser(JsonObject messageJson, String recipientUsername) {
        Set<Session> recipientSessions = sessions.get(recipientUsername);
        if (recipientSessions != null) {
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
            return true; // Mensagem enviada para pelo menos uma sessão
        }
        return false; // Não há sessões ativas para o destinatário
    }

    /**
     * Arquiva a mensagem enviada no banco de dados e constrói o JSON da mensagem.
     *
     * @param message   O conteúdo da mensagem.
     * @param sender    O nome do remetente.
     * @param recipient O nome do destinatário.
     * @return O JSON da mensagem arquivada ou um JSON de erro, se o arquivamento falhar.
     */
    public JsonObject archiveNewMessage(String message, String sender, String recipient) {
        LocalDateTime timestamp = LocalDateTime.now();
        MessageDto messageDto = new MessageDto(message, sender, recipient, timestamp);
        messageDto = messageBean.newMessage(messageDto);

        if (messageDto != null) {
            return Json.createObjectBuilder()
                    .add("type", "MESSAGE")
                    .add("id", messageDto.getMessageId())
                    .add("sender", sender)
                    .add("recipient", recipient)
                    .add("message", message)
                    .add("timestamp", timestamp.toString())
                    .build();
        }
        // Retorna um JSON de erro em caso de falha
        return Json.createObjectBuilder()
                .add("type", "ERROR")
                .add("message", "Failed to archive message")
                .build();
    }

    /**
     * Verifica se a mensagem recebida é válida.
     *
     * @param jsonMessage O objeto JSON da mensagem.
     * @return `true` se a mensagem for válida; caso contrário, `false`.
     */
    private boolean checkIfValidMessage(JsonObject jsonMessage) {
        return jsonMessage.containsKey("recipient") &&
                jsonMessage.containsKey("message") &&
                jsonMessage.get("recipient") != null &&
                jsonMessage.get("message") != null &&
                !jsonMessage.getString("recipient").trim().isEmpty() &&
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