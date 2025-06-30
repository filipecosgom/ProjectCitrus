package pt.uc.dei.proj5.websocket;

import jakarta.inject.Inject;
import jakarta.websocket.Session;
import jakarta.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pt.uc.dei.proj5.beans.TokenBean;
import pt.uc.dei.proj5.dto.TokenDto;
import pt.uc.dei.proj5.dto.TokenType;
import pt.uc.dei.proj5.dto.UserDto;
import pt.uc.dei.proj5.dto.UserAccountState;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
/*
{
    "type" : "AUTHENTICATION",
    "token": "ttIMhhGurQMoiP7H-_c5_EsCWATkmVJ5"
}
 */

/*
{
    "type": "MESSAGE",
    "recipient": "anotherUser",
    "message": "Hello there!"
}

 */
/**
 * Classe utilitária responsável por gerenciar a autenticação de usuários
 * no contexto de sockets WebSocket.
 * Proporciona métodos para autenticar sessões, enviar mensagens de erro/sucesso,
 * e associar sessões aos usuários autenticados.
 */
public class WebSocketAuthentication {

    // Logger para registrar eventos e erros relacionados à autenticação
    private static final Logger logger = LogManager.getLogger(WebSocketAuthentication.class);

    /**
     * Método responsável por autenticar uma sessão WebSocket com base em um token recebido.
     * Se a autenticação for bem-sucedida, a sessão será associada ao usuário.
     *
     * @param session   Sessão WebSocket que está solicitando autenticação.
     * @param jsonMessage Mensagem JSON enviada pelo cliente contendo o token de autenticação.
     * @param tokenBean  Bean usado para validar o token de autenticação no sistema.
     * @param sessions   Mapa que associa nomes de usuários às suas sessões WebSocket.
     * @return `true` se a autenticação foi bem-sucedida, ou `false` caso contrário.
     */
    public static boolean authenticate(Session session, JsonObject jsonMessage,
                                       TokenBean tokenBean, HashMap<String, Set<Session>> sessions) {
        // Verifica se o JSON enviado contém o campo "token"
        if (!jsonMessage.containsKey("token") || jsonMessage.isNull("token")) {
            sendErrorMessage(session, "Missing authentication token");
            return false;
        }

        // Recupera o token enviado
        String token = jsonMessage.getString("token");
        TokenDto tokenDto = new TokenDto();
        tokenDto.setTokenValue(token);

        try {
            // Valida o token para recuperar informações do usuário
            UserDto user = tokenBean.checkToken(tokenDto);
            if (user == null) {
                logger.error("Invalid authentication token");
                sendErrorMessage(session, "Invalid authentication token");
                return false;
            }

            // Verifica se a conta do usuário está ativa e não excluída
            if (user.getState().equals(UserAccountState.EXCLUDED) || user.getState().equals(UserAccountState.INACTIVE)) {
                logger.info("Authentication failed: User {} has inactive or excluded account", user.getUsername());
                sendErrorMessage(session, "Excluded or inactive user");
                return false;
            }

            // Adiciona a sessão ao mapa de sessões do usuário
            sessions.computeIfAbsent(user.getUsername(), k -> new HashSet<>()).add(session);
            logger.info("User {} authenticated in WebSocket", user.getUsername());

            // Envia mensagem de sucesso de autenticação
            sendSuccessMessage(session, user.getUsername());
            return true;

        } catch (Exception e) {
            logger.error("Authentication failed", e);
            sendErrorMessage(session, e.getMessage());
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
     * @param username Nome de usuário autenticado.
     */
    private static void sendSuccessMessage(Session session, String username) {
        try {
            // Envia uma mensagem indicando que a autenticação foi realizada com sucesso
            session.getBasicRemote().sendText("{ \"type\": \"AUTHENTICATED\", \"username\": \"" + username + "\" }");
        } catch (IOException e) {
            logger.error("Failed to send authentication success message", e);
        }
    }

    /**
     * Método para localizar o nome de usuário associado a uma determinada sessão WebSocket.
     *
     * @param sessions Mapa que associa nomes de usuários às suas sessões WebSocket.
     * @param session  Sessão WebSocket a ser buscada.
     * @return Nome do usuário associado à sessão, ou `null` se não for encontrado.
     */
    public static String findUsernameBySession(HashMap<String, Set<Session>> sessions, Session session) {
        for (Map.Entry<String, Set<Session>> entry : sessions.entrySet()) {
            if (entry.getValue().contains(session)) {
                return entry.getKey(); // Retorna o nome do usuário relacionado à sessão
            }
        }
        return null; // Retorna null caso nenhuma correspondência seja encontrada
    }
}