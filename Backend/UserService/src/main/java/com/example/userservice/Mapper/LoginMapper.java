package com.example.userservice.Mapper;

import com.example.userservice.DTO.LoginRequest;
import com.example.userservice.Model.User;
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