package com.example.backendservice.Repository;

import com.example.backendservice.Model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User,String> {
    User findByEmail(String username);

    User findByUserId(String id);
}
