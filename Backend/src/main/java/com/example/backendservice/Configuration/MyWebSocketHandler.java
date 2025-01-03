package com.example.backendservice.Configuration;

import com.example.backendservice.Model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class MyWebSocketHandler extends TextWebSocketHandler {

    private final static ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final static ConcurrentHashMap<String, String> onlineUsers = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = getUserIdFromSession(session);
        if (userId != null) {
            addUserSession(session, userId);
            broadcastOnlineUsers();
            log.info("User connected: {} with sessionId: {}", userId, session.getId());
        } else {
            log.warn("UserId is missing in the connection request.");
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("Received message: {}", payload);
        session.sendMessage(new TextMessage(payload));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        removeUserSession(session);
        broadcastOnlineUsers();
        log.info("User disconnected: {}", session.getId());
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
                log.info("session open: {}", session.isOpen());
                log.info("Sending message to session {}: {}", session.getId(), message.getPayload());
                session.sendMessage(message);
                log.info("Message sent to session {}", session.getId());
            } else {
                log.warn("Session {} is closed", session.getId());
            }
        } catch (Exception e) {
            log.error("Error sending message to session {}: {}", session.getId(), e.getMessage(), e);
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

    public void broadcastMessage(Message message) {
        String jsonMessage = String.format("{\"type\":\"NEW_MESSAGE\",\"message\":\"%s\",\"senderId\":\"%s\"}",
                message.getMessage(), message.getSenderId());
        TextMessage textMessage = new TextMessage(jsonMessage);
        log.info("Broadcasting message from senderId {}: {}", message.getSenderId(), message.getMessage());
        log.info("sessions: {}", sessions);
        sessions.values().forEach(session -> {
            sendMessage(session, textMessage);
            log.info("Message sent to session {}", session.getId());
        });
        log.info("Message broadcasted to all sessions.");
    }
}