package com.example.notificationservice.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class MyWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(MyWebSocketHandler.class);

    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> onlineUsers = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = getUserIdFromSession(session);
        if (userId != null) {
            addUserSession(session, userId);
            broadcastOnlineUsers();
            logger.info("User connected: {} with sessionId: {}", userId, session.getId());
        } else {
            logger.warn("UserId is missing in the connection request.");
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        logger.info("Received message: {}", payload);
        session.sendMessage(new TextMessage("Server response: " + payload));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        removeUserSession(session);
        broadcastOnlineUsers();
        logger.info("User disconnected: {}", session.getId());
    }

    private void addUserSession(WebSocketSession session, String userId) {
        sessions.put(session.getId(), session);
        onlineUsers.put(session.getId(), userId);
    }

    private void removeUserSession(WebSocketSession session) {
        sessions.remove(session.getId());
        onlineUsers.remove(session.getId());
    }

    private void broadcastOnlineUsers() {
        String onlineUsersList = String.join(",", onlineUsers.values());
        TextMessage message = new TextMessage("{\"type\":\"ONLINE_USERS\",\"users\":\"" + onlineUsersList + "\"}");
        sessions.values().forEach(session -> sendMessage(session, message));
    }

    private void sendMessage(WebSocketSession session, TextMessage message) {
        try {
            if (session.isOpen()) {
                session.sendMessage(message);
            }
        } catch (Exception e) {
            logger.error("Error sending message to session {}: {}", session.getId(), e.getMessage());
        }
    }

    private String getUserIdFromSession(WebSocketSession session) {
        if (session.getUri() != null && session.getUri().getQuery() != null) {
            String[] queryParams = session.getUri().getQuery().split("&");
            for (String param : queryParams) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2 && "userId".equals(keyValue[0])) {
                    return keyValue[1];
                }
            }
        }
        return null;
    }
}
