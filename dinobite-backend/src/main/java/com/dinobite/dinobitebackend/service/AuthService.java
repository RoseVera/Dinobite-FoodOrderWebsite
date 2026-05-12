package com.dinobite.dinobitebackend.service;
import com.dinobite.dinobitebackend.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dinobite.dinobitebackend.config.JwtService;
import com.dinobite.dinobitebackend.dto.AuthRequest;
import com.dinobite.dinobitebackend.dto.RegisterRequest;
import com.dinobite.dinobitebackend.model.User;
import com.dinobite.dinobitebackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.mail.MailException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import java.util.*;

/**
 * Service class responsible for handling authentication and user registration logic.
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    // Dependency for accessing user data from the database.
    private final UserRepository userRepository;

    // Service for generating and validating JWT tokens
    private final JwtService jwtService;

    // Password encoder for securely hashing user passwords.
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final Map<String, String> resetCodes = new HashMap<>();

    @Autowired
    private JavaMailSender mailSender;

    public void sendResetCode(String email) {
        Optional<User> userOpt = userRepository.findByMail(email);
        if (userOpt.isEmpty()) {
            throw new BusinessException("No user found with this email.");
        }

        String code = String.format("%06d", new Random().nextInt(999999));
        resetCodes.put(email, code);

        sendEmail(email, "🦕 DinoBite Reset Password Code 🦕", " Your Code: " + code);
    }

    private void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (MailException ex) {
            throw new BusinessException("Failed to send email. Please check the email address.");
        }
    }

    public boolean validateCode(String email, String code) {
        return code.equals(resetCodes.get(email));
    }

    public void resetPassword(String email, String code, String newPassword) {
        if (!validateCode(email, code)) {
            throw new BusinessException("Invalid Code");
        }

        User user = userRepository.findByMail(email)
                .orElseThrow(() -> new BusinessException("No user found."));

        String encodedPassword = new BCryptPasswordEncoder().encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);

        resetCodes.remove(email); // Kod artık kullanılmasın
    }

    /**
     * Registers a new user by creating a User entity from the provided registration request.
     * The password is securely hashed before saving.
     * @throws BusinessException
     * @param request the registration request containing user details (mail, name, password, type).
     * @return the saved User entity.
     */
    public User register(RegisterRequest request) {
        logger.warn(" IN AUTH SERVICE");
        if (userRepository.existsByMail(request.getMail())) {
            logger.warn("HEEEY HEEEEY Email already in use: {}", request.getMail());
        throw new BusinessException("Email already in use");
       }

        User user = new User();
        user.setMail(request.getMail());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setType(request.getType());

        userRepository.save(user);
        return user;
    }


    /**
     * Authenticates a user by validating their email and password.
     *
     * @param request the authentication request containing email and password.
     * @return the authenticated User entity if credentials are valid.
     * @throws RuntimeException if the user is not found or the password is invalid.
     */
    public User authenticate(AuthRequest request) {
    User user = userRepository.findByMail(request.getMail())
            .orElseThrow(() -> new RuntimeException("User not found"));

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        throw new RuntimeException("Invalid password");
    }

    return user;
    }

}
