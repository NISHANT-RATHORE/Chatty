package com.example.backendservice.Service;

import com.example.backendservice.DTO.ImageModel;
import com.example.backendservice.Model.Message;
import com.example.backendservice.Model.User;
import com.example.backendservice.Repository.MessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;
    private final CloudinaryService cloudinaryService;
    private final UserService userService;

    public MessageService(MessageRepository messageRepository, CloudinaryService cloudinaryService, UserService userService) {
        this.messageRepository = messageRepository;
        this.cloudinaryService = cloudinaryService;
        this.userService = userService;
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
        ImageModel imagemodel = ImageModel.builder().file(image).build();
        if(image != null){
            imageUrl = uploadImage(imagemodel);
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

    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    public String getId(String jwt) {
        return userService.getId(jwt);
    }
}
