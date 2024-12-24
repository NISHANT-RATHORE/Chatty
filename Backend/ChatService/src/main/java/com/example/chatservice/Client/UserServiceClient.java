package com.example.chatservice.Client;

import com.example.chatservice.DTO.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "user-service",url = "http://localhost:9001/user")
public interface UserServiceClient {
    @GetMapping("/getAllUsers")
    ResponseEntity<List<User>> getAll();
}
