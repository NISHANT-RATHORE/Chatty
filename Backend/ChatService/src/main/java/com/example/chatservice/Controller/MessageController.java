package com.example.chatservice.Controller;

import com.example.chatservice.Client.UserServiceClient;
import com.example.chatservice.DTO.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/messages")
@Slf4j
public class MessageController {

    private final UserServiceClient userServiceClient;

    public MessageController(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUserData(){
        try{
            log.info("Received request to get all users...");
            List<User> users = userServiceClient.getAll().getBody();
            log.info("Returning all users...");
            return ResponseEntity.ok(users);
        } catch (Exception e){
            log.error("Error retrieving doctor", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}