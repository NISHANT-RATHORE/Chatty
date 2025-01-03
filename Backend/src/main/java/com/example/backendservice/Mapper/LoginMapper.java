package com.example.backendservice.Mapper;

import com.example.backendservice.DTO.LoginRequest;
import com.example.backendservice.Model.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LoginMapper {
    public static User loginToUser(LoginRequest request) {
        return User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
    }
}