package com.example.userservice.Controller;

import com.example.userservice.DTO.AddUserRequest;
import com.example.userservice.DTO.LoginRequest;
import com.example.userservice.Mapper.LoginMapper;
import com.example.userservice.Model.User;
import com.example.userservice.Service.UserService;
import com.example.userservice.Util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@Slf4j
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserController(UserService userService, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

   @PostMapping("/register")
    public ResponseEntity<User> addUser(@RequestBody AddUserRequest request) {
        try {
            log.info("Received request to add user: {}", request.getEmail());
            User user = userService.addUser(request);
            log.info("User added successfully: {}", user.getEmail());

            // Authenticate the user and generate JWT token
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = userService.loadUserByUsername(request.getEmail());
            String jwt = jwtUtil.generateToken(userDetails.getUsername());

            // Create a cookie with the JWT token
            ResponseCookie jwtCookie = ResponseCookie.from("jwt", jwt)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60)  // 7 days
                    .build();
            log.info("JWT token generated and added to cookie for user: {}", user.getEmail());
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body(user);
        } catch (Exception e) {
            log.error("Error occurred while adding user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<User> loginUser(@RequestBody LoginRequest request) {
        User user = LoginMapper.loginToUser(request);
        log.info("Login attempt for user: {}", user.getEmail());
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = userService.loadUserByUsername(user.getEmail());
            String jwt = jwtUtil.generateToken(userDetails.getUsername());

            // Create a cookie with the JWT token
            ResponseCookie jwtCookie = ResponseCookie.from("jwt", jwt)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60)  // 7 days
                    .build();

            log.info("User logged in successfully: {}", user.getEmail());
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body(user);
        } catch (AuthenticationException e) {
            log.warn("Login failed for user: {}", user.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser() {
        // Remove the JWT cookie by setting its max age to 0
        ResponseCookie jwtCookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        log.info("User logged out successfully");
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body("User logged out successfully");
    }


    @GetMapping("/check-auth")
    public ResponseEntity<String> checkAuthentication(@CookieValue(name = "jwt", required = false) String token) {
        if (token != null && jwtUtil.validateToken(token)) {
            String username = jwtUtil.extractUsername(token);
            log.info("User is authenticated: {}", username);
            return ResponseEntity.ok("User is authenticated");
        } else {
            log.warn("User is not authenticated");
            return ResponseEntity.status(401).body("User is not authenticated");
        }
    }

}