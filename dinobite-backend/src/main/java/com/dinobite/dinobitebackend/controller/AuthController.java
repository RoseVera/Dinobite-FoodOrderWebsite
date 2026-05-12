package com.dinobite.dinobitebackend.controller;

import com.dinobite.dinobitebackend.config.JwtService;
import com.dinobite.dinobitebackend.model.User;
import com.dinobite.dinobitebackend.repository.UserRepository;
import com.dinobite.dinobitebackend.exception.BusinessException;
import com.dinobite.dinobitebackend.dto.AuthRequest;
import com.dinobite.dinobitebackend.dto.AuthResponse;
import com.dinobite.dinobitebackend.dto.RegisterRequest;
import com.dinobite.dinobitebackend.service.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import java.util.Map;

/**
 * AuthController.java
 * This class handles authentication-related requests such as registration, login, and fetching user details.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    // Constructor-based dependency injection
    private final AuthService authService;
    private final JwtService jwtService;

    // UserRepository is used to fetch user details from the database
    private final UserRepository userRepository;

    /**
     * Register a new user
     * @param request The registration request containing user details
     * @param response The HTTP response to set the token cookie
     * @return AuthResponse containing user details and a success message
     */
    @PostMapping("/register")
    public AuthResponse register( @RequestBody RegisterRequest request, HttpServletResponse response) {
         if (request.getMail() == null || request.getPassword() == null || request.getName() == null || request.getType() == null) {
        throw new RuntimeException("Missing required fields");
    }
        User user = authService.register(request);
        String token = jwtService.generateToken(user.getMail()); // Generate a JWT token for the user

        setTokenCookie(response, token); // Set the token in a cookie

        return new AuthResponse(
            "Registration successful",
            user.getId(),
            user.getName(),
            user.getType().name()
        
        );
    }

    /**
     * Fetch the current user's details
     * @param token The JWT token from the cookie
     * @return AuthResponse containing user details
     */
    @GetMapping("/me")
    public AuthResponse me(@CookieValue(name = "token", required = false) String token) {
        if (token == null) {
            throw new RuntimeException("No token found");
        }

        String email = jwtService.extractMail(token);
        User user = userRepository.findByMail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new AuthResponse(
            "User fetched successfully",
            user.getId(),
            user.getName(),
            user.getType().name()
        );
    }

    /**
     * Log in a user
     * @param request The login request containing user credentials
     * @param response The HTTP response to set the token cookie
     * @return AuthResponse containing user details and a success message
     */
    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request, HttpServletResponse response) {
    User user = authService.authenticate(request);
    String token = jwtService.generateToken(user.getMail());
    setTokenCookie(response, token);

    return new AuthResponse(
        "Login successful",
        user.getId(),
        user.getName(),
        user.getType().name()
    );
    }

    /**
     * Log out a user
     * @param response The HTTP response to clear the token cookie
     * @return AuthResponse containing a success message
     */
   @PostMapping("/logout")
    public AuthResponse logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return new AuthResponse("Logout successful", null, null, null);
    }

    /**
     * Set the JWT token in a cookie
     * @param response The HTTP response to set the cookie
     * @param token The JWT token to be set
     */
    private void setTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(86400); // 1 day
        response.addCookie(cookie);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        authService.sendResetCode(email);
        return ResponseEntity.ok("Şifre sıfırlama kodu e-posta adresinize gönderildi.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        String newPassword = request.get("newPassword");

        authService.resetPassword(email, code, newPassword);
        return ResponseEntity.ok("Şifreniz başarıyla sıfırlandı.");
    }


}
