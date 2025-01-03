package com.example.backendservice.Model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Message {

    @Id
    String id;
    String chatId;
    String senderId;
    String receiverId;
    String message;
    String image;
    LocalDateTime createdAt;
}
