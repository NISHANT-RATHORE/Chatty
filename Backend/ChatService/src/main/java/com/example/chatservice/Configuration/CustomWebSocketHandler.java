// package com.example.chatservice.Configuration;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.messaging.simp.SimpMessagingTemplate;
// import org.springframework.web.socket.CloseStatus;
// import org.springframework.web.socket.WebSocketSession;
// import org.springframework.web.socket.handler.AbstractWebSocketHandler;
// import org.springframework.stereotype.Component;

// import java.util.List;
// import java.util.Map;
// import java.util.concurrent.ConcurrentHashMap;
// import java.util.stream.Collectors;

// @Component
// public class CustomWebSocketHandler extends AbstractWebSocketHandler {

//     private static final Logger logger = LoggerFactory.getLogger(CustomWebSocketHandler.class);
//     private final Map<String, String> userSocketMap = new ConcurrentHashMap<>();
//     private final SimpMessagingTemplate messagingTemplate;

//     public CustomWebSocketHandler(SimpMessagingTemplate messagingTemplate) {
//         this.messagingTemplate = messagingTemplate;
//     }

//     @Override
//     public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//         String userId = getUserIdFromSession(session);
//         if (userId != null) {
//             String sessionId = session.getId();
//             userSocketMap.put(userId, sessionId);
//             logger.info("User connected: {} with sessionId: {}", userId, sessionId);
//             broadcastOnlineUsers();
//         } else {
//             logger.warn("UserId is missing in the connection request.");
//         }
//     }

//     @Override
//     public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//         String userId = getUserIdFromSession(session);
//         if (userId != null) {
//             userSocketMap.remove(userId);
//             logger.info("User disconnected: {}", userId);
//             broadcastOnlineUsers();
//         } else {
//             logger.warn("UserId is missing in the disconnection request.");
//         }
//     }

//     private String getUserIdFromSession(WebSocketSession session) {
//         if (session.getUri() != null && session.getUri().getQuery() != null) {
//             String[] queryParams = session.getUri().getQuery().split("&");
//             for (String param : queryParams) {
//                 String[] keyValue = param.split("=");
//                 if (keyValue.length == 2 && "userId".equals(keyValue[0])) {
//                     return keyValue[1];
//                 }
//             }
//         }
//         return null;
//     }

//     private void broadcastOnlineUsers() {
//         List<String> onlineUsers = userSocketMap.keySet().stream().collect(Collectors.toList());
//         messagingTemplate.convertAndSend("/topic/online-users", onlineUsers);
//         logger.info("Broadcasted online users: {}", onlineUsers);
//     }
// }
