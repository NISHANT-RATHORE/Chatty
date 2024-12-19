package com.example.userservice.Service;

import com.example.userservice.DTO.AddUserRequest;
import com.example.userservice.DTO.ImageModel;
import com.example.userservice.Mapper.UserMapper;
import com.example.userservice.Model.User;
import com.example.userservice.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, CloudinaryService cloudinaryService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.cloudinaryService = cloudinaryService;
    }

    public User addUser(AddUserRequest request) {
        User user = UserMapper.mapToUser(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(List.of("USER"));
        if(userRepository.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("User already exists");
        }
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if (user != null) {
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getEmail())
                    .password(user.getPassword())
                    .roles(user.getRoles().toArray(new String[0]))
                    .build();
        }
        throw new UsernameNotFoundException(username.concat(" user not found"));
    }


    public String uploadImage(ImageModel imageModel) {
        try {
            if (imageModel.getFile().isEmpty()) {
                return null;
            }
            String imageUrl = cloudinaryService.uploadFile(imageModel.getFile(), "DoctorPhotos");
            if (imageUrl == null) {
                return null;
            }
            return imageUrl;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}