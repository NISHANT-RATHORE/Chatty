package com.example.backendservice.Repository;

import com.example.backendservice.Model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message,String> {
    List<Message> findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderByCreatedAt(String senderId, String recievedId, String recievedId1, String senderId1);
//    List<Message> findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByCreatedAt(String senderId, String receiverId, String receiverId1, String senderId1);
}
