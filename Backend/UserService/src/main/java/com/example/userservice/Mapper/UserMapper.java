package com.example.userservice.Mapper;

import com.example.userservice.DTO.AddUserRequest;
import com.example.userservice.Model.User;
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