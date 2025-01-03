package com.example.backendservice.Mapper;

import com.example.backendservice.DTO.AddUserRequest;
import com.example.backendservice.Model.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {
    public static User mapToUser(AddUserRequest request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
    }
}