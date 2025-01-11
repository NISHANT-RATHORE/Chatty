package com.example.backendservice.DTO;

import com.example.backendservice.Model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    User user;
    String token;
}
