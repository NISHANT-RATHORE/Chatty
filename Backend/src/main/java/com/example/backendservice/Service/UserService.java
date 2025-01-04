package com.example.backendservice.Service;

import com.example.backendservice.DTO.AddUserRequest;
import com.example.backendservice.DTO.ImageModel;
import com.example.backendservice.Mapper.UserMapper;
import com.example.backendservice.Model.User;
import com.example.backendservice.Repository.UserRepository;
import com.example.backendservice.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, CloudinaryService cloudinaryService, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.cloudinaryService = cloudinaryService;
        this.jwtUtil = jwtUtil;
    }

    public User addUser(AddUserRequest request) {
        User user = UserMapper.mapToUser(request);
        user.setUserId(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(List.of("USER"));
        user.setCreatedAt(LocalDate.now());
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

    public String updateProfile(MultipartFile image,User user) {
        ImageModel userImage = ImageModel.builder().file(image).build();
        if(userImage.getFile() == null) {
            throw new IllegalArgumentException("Image is empty");
        }
        String imageUrl = uploadImage(userImage);
        user.setImage(imageUrl);
        userRepository.save(user);
        return "Profile updated successfully";
    }

    public User getUserByEmail(String username) {
        return userRepository.findByEmail(username);
    }

    public List<User> getAllUsers(String token) {
        User user = userRepository.findByEmail(jwtUtil.extractUsername(token));
        return userRepository.findAll().stream()
                .filter(u -> !u.getUserId().equals(user.getUserId()))
                .toList();
    }

    public String getId(String jwt) {
        User user = getUserByEmail(jwtUtil.extractUsername(jwt));
        return user.getUserId();
    }
}