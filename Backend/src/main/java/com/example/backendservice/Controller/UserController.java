package com.example.backendservice.Controller;

import com.example.backendservice.DTO.AddUserRequest;
import com.example.backendservice.DTO.LoginRequest;
import com.example.backendservice.DTO.Response;
import com.example.backendservice.Mapper.LoginMapper;
import com.example.backendservice.Model.User;
import com.example.backendservice.Repository.UserRepository;
import com.example.backendservice.Service.UserService;
import com.example.backendservice.Util.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping("/user")
@CrossOrigin(origins = "${frontend.url}", allowCredentials = "true")
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Autowired
    public UserController(UserService userService, AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository1) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository1;
    }

    @PostMapping("/register")
    public ResponseEntity<Response> addUser(@RequestBody AddUserRequest request, HttpServletResponse response) {
        try {
            log.info("Received request to add user: {}", request.getEmail());
            User user = userService.addUser(request);
            log.info("User added successfully: {}", user.getEmail());
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = userService.loadUserByUsername(request.getEmail());
            String jwt = jwtUtil.generateToken(userDetails.getUsername());
            log.info("JWT token generated and added to cookie for user: {}", user.getEmail());
            return ResponseEntity.ok(new Response(user, jwt));
        } catch (Exception e) {
            log.error("Error occurred while adding user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Response> loginUser(@RequestBody LoginRequest request) {
        User user = LoginMapper.loginToUser(request);
        log.info("Login attempt for user: {}", user.getEmail());
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = userService.loadUserByUsername(user.getEmail());
            String jwt = jwtUtil.generateToken(userDetails.getUsername());
            log.info("User logged in successfully: {}", user.getEmail());
            return ResponseEntity.ok(new Response(user, jwt));
        } catch (AuthenticationException e) {
            log.warn("Login failed for user: {}", user.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/check-auth")
    public ResponseEntity<String> checkAuthentication(@RequestHeader(value = "Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String username = jwtUtil.extractUsername(token);
            log.info("User is authenticated: {}", username);
            return ResponseEntity.ok("User is authenticated");
        } else {
            log.warn("User is not authenticated");
            return ResponseEntity.status(401).body("User is not authenticated");
        }
    }

    @PutMapping("/update-profile")
    public ResponseEntity<String> updateProfile(@RequestParam(required = false) MultipartFile image,
            @RequestHeader(value = "Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String username = jwtUtil.extractUsername(token);
            if (username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated or not found");
            }
            User user = userRepository.findByEmail(username);
            String imageUrl = userService.updateProfile(image, user);
            if (imageUrl == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Image upload failed");
            }
            return ResponseEntity.ok(imageUrl);
        }
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(@RequestHeader(value = "Authorization") String token) {
        log.info("token: {}", token);
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Remove "Bearer " prefix
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.extractUsername(token);
                User user = userService.getUserByEmail(username);
                return ResponseEntity.ok(user);
            }
        }
        log.warn("User is not authenticated");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}