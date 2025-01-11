package com.example.backendservice.Controller;

import com.example.backendservice.Configuration.MyWebSocketHandler;
import com.example.backendservice.Model.Message;
import com.example.backendservice.Model.User;
import com.example.backendservice.Service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/messages")
@Slf4j
@CrossOrigin(origins = "${frontend.url}", allowCredentials = "true")
public class MessageController {

    private final MessageService messageService;
    private final MyWebSocketHandler myWebSocketHandler;

    public MessageController(MessageService messageService, MyWebSocketHandler myWebSocketHandler) {
        this.messageService = messageService;
        this.myWebSocketHandler = myWebSocketHandler;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUserData(@RequestHeader(value = "Authorization") String token) {
        try {
            if (token != null && token.startsWith("Bearer")) {
                token = token.substring(7);
                List<User> users = messageService.getAllUsers(token);
                return ResponseEntity.ok(users);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

        } catch (Exception e) {
            log.error("Error retrieving users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/getId")
    public ResponseEntity<String> getId(@RequestHeader(value = "Authorization") String jwt) {
        try {
            if (jwt != null && jwt.startsWith("Bearer ")) {
                String token = jwt.substring(7);
                String id = messageService.getId(token);
                return ResponseEntity.ok(id);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } catch (Exception e) {
            log.error("Error retrieving id", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<Message>> getMessage(@PathVariable("id") String receiverId,
            @RequestHeader(value = "Authorization") String jwt) {
        try {
            if (jwt != null && jwt.startsWith("Bearer ")) {
                String token = jwt.substring(7);
                String senderId = messageService.getId(token);
                if (senderId == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }
                List<Message> messages = messageService.getMessagesBetweenUsers(senderId,
                        receiverId);
                return ResponseEntity.ok(messages);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

            }
        } catch (Exception e) {
            log.error("Error retrieving messages", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/send/{userId}")
    public ResponseEntity<Message> sendMessage(@RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @PathVariable("userId") String receiverId, @RequestHeader(value = "Authorization") String jwt) {
        try {
            if (jwt != null && jwt.startsWith("Bearer ")) {
                String token = jwt.substring(7);

                log.info("Received request to send message...");
                String senderId = messageService.getId(token);
                if (senderId == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }
                Message newMessage = messageService.sendMessage(senderId, receiverId, text,
                        image);
                myWebSocketHandler.broadcastMessage(newMessage); // Broadcast the message to
                log.info("Returning message...");
                return ResponseEntity.ok(newMessage);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

            }
        } catch (Exception e) {
            log.error("Error sending message", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
