package com.example.chatservice.DTO;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    String userId;
    String name;
    String email;
    String password;
    String image;
    LocalDate createdAt;
}
