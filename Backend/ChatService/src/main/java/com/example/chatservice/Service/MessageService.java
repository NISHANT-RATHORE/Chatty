package com.example.chatservice.Service;

import com.example.chatservice.DTO.ImageModel;
import com.example.chatservice.DTO.User;
import com.example.chatservice.Model.Message;
import com.example.chatservice.Repository.MessageRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final CloudinaryService cloudinaryService;

    public MessageService(MessageRepository messageRepository, CloudinaryService cloudinaryService) {
        this.messageRepository = messageRepository;
        this.cloudinaryService = cloudinaryService;
    }

    public List<Message> getMessagesBetweenUsers(String senderId, String recievedId) {
        return messageRepository.findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderByCreatedAt(senderId, recievedId, recievedId, senderId);
    }

    public String uploadImage(ImageModel imageModel) {
        try {
            if (imageModel.getFile().isEmpty()) {
                return null;
            }
            String imageUrl = cloudinaryService.uploadFile(imageModel.getFile(), "ChatApp");
            if (imageUrl == null) {
                return null;
            }
            return imageUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Message sendMessage(String senderId, String userId, String text, MultipartFile image) {
        if (text == null && image == null) {
            throw new IllegalArgumentException("Message cannot be empty");
        }
        String imageUrl = null;
        if(image != null){
            imageUrl = uploadImage(new ImageModel(image));
            log.info("Image URL: " + imageUrl);
        }


        Message message = Message.builder()
            .senderId(senderId)
            .receiverId(userId)
            .chatId(UUID.randomUUID().toString())  // Generate a unique chat ID
            .message(text)  // Save the text
            .image(imageUrl)  // Save the image URL if image exists
            .createdAt(LocalDateTime.now())  // Save the current date
            .build();
        return messageRepository.save(message);
    }
}
