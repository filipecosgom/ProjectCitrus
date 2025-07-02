package pt.uc.dei.websocket;

import jakarta.ejb.EJB;
import jakarta.json.JsonObject;
import jakarta.websocket.Session;
import jakarta.websocket.server.HandshakeRequest;
import pt.uc.dei.services.AuthenticationService;
import pt.uc.dei.utils.JWTUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
/**
 * Classe utilitária responsável por gerenciar a autenticação de usuários
 * no contexto de sockets WebSocket.
 * Proporciona métodos para autenticar sessões, enviar mensagens de erro/sucesso,
 * e associar sessões aos usuários autenticados.
 */
public class WebSocketAuthentication {

    @EJB
    AuthenticationService authenticationService;

    // Logger para registrar eventos e erros relacionados à autenticação
    private static final Logger logger = LogManager.getLogger(WebSocketAuthentication.class);

    /**
     * Authenticates a WebSocket session using the HandshakeRequest.
     * Extracts the JWT from cookies, validates it, and registers the session by user ID.
     *
     * @param session   The WebSocket session to authenticate.
     * @param request   The HandshakeRequest containing cookies.
     * @param sessions  The map of user IDs to their WebSocket sessions.
     * @return true if authentication is successful, false otherwise.
     */
    public boolean authenticate(Session session, HandshakeRequest request, HashMap<Long, Set<Session>> sessions) {
        Long userId = JWTUtil.getUserIdFromToken(request);
        authenticationService.setUserOnline(userId);
        if (userId != null) {
            sessions.computeIfAbsent(userId, k -> new HashSet<>()).add(session);
            logger.info("User {} authenticated in WebSocket via cookie", userId);
            sendSuccessMessage(session, userId);
            return true;
        } else {
            logger.warn("WebSocket authentication failed: missing or invalid JWT");
            sendErrorMessage(session, "Unauthorized: missing or invalid JWT");
            return false;
        }
    }


    /**
     * Método auxiliar para enviar uma mensagem de erro ao cliente WebSocket.
     * A mensagem informa que a autenticação falhou.
     *
     * @param session Sessão WebSocket que será notificada.
     * @param message Mensagem de erro a ser enviada ao cliente.
     */
    private static void sendErrorMessage(Session session, String message) {
        try {
            // Envia o erro para a sessão no formato JSON
            session.getBasicRemote().sendText("{ \"type\": \"AUTH_FAILED\", \"message\": \"" + message + "\" }");
        } catch (IOException e) {
            logger.error("Failed to close session after authentication failure", e);
        }
    }

    /**
     * Método auxiliar para enviar mensagens informativas para o cliente WebSocket.
     *
     * @param session Sessão WebSocket que será notificada.
     * @param message Mensagem informativa a ser enviada ao cliente.
     */
    private static void sendInfoMessage(Session session, String message) {
        try {
            // Envia a mensagem informativa no formato JSON
            session.getBasicRemote().sendText("{ \"type\": \"AUTH_INFO\", \"message\": \"" + message + "\" }");
        } catch (IOException e) {
            logger.error("Failed to inform user", e);
        }
    }

    /**
     * Método auxiliar para enviar uma mensagem de sucesso ao cliente após autenticação.
     *
     * @param session  Sessão WebSocket que será notificada.
     * @param userId   ID do usuário autenticado.
     */
    private static void sendSuccessMessage(Session session, Long userId) {
        try {
            // Envia uma mensagem indicando que a autenticação foi realizada com sucesso
            session.getBasicRemote().sendText("{ \"type\": \"AUTHENTICATED\", \"userId\": " + userId + " }");
        } catch (IOException e) {
            logger.error("Failed to send authentication success message", e);
        }
    }

    /**
     * Método para localizar o ID do usuário associado a uma determinada sessão WebSocket.
     *
     * @param sessions Mapa que associa IDs de usuários às suas sessões WebSocket.
     * @param session  Sessão WebSocket a ser buscada.
     * @return ID do usuário associado à sessão, ou `null` se não for encontrado.
     */
    public static Long findUserIdBySession(HashMap<Long, Set<Session>> sessions, Session session) {
        for (Map.Entry<Long, Set<Session>> entry : sessions.entrySet()) {
            if (entry.getValue().contains(session)) {
                return entry.getKey();
            }
        }
        return null;
    }
}