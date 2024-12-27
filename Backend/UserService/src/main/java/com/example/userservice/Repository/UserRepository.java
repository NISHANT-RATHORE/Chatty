package com.example.userservice.Repository;

import com.example.userservice.Model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User,String> {
    User findByEmail(String username);

    User findByUserId(String id);
}
