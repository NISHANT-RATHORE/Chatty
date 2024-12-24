package com.example.userservice.DTO;

import lombok.Data;


import java.time.LocalDate;

@Data
public class UserData {
    // Getters and setters
    private String userId;
    private String name;
    private String email;
    private String password;
    private String image;
    private LocalDate createdAt;

    public UserData(String userId, String name, String email, String password, String image, LocalDate createdAt) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.image = image;
        this.createdAt = createdAt;
    }

}