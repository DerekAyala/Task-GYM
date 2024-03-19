package com.epam.taskgym.service;

import com.epam.taskgym.entity.User;
import com.epam.taskgym.exception.NotFoundException;
import com.epam.taskgym.helpers.Builders;
import com.epam.taskgym.helpers.Validations;
import com.epam.taskgym.models.RegisterResponse;
import com.epam.taskgym.models.UserResponse;
import com.epam.taskgym.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private Optional<User> findByUsername(String username) {
        Validations.validateUsername(username);
        LOGGER.info("Transaction Id: {}, Finding user by username: {}", MDC.get("transactionId"),username);
        return userRepository.findByUsername(username);
    }

    @Transactional
    public UserResponse createUser(String firstName, String lastName, String role) {
        LOGGER.info("Transaction Id: {}, Method: {}, Creating user: {} {}", MDC.get("transactionId"), MDC.get("MethodName"), firstName, lastName);
        Validations.validateUserDetails(firstName, lastName);
        String username = generateUniqueUsername(firstName.toLowerCase(), lastName.toLowerCase());
        String password = Builders.generateRandomPassword();
        User user = Builders.buildUser(firstName, lastName, username, passwordEncoder.encode(password), role);
        saveUser(user);
        LOGGER.info("Successfully created user: {}", user.getUsername());
        return new UserResponse(user, password);
    }

    @Transactional
    public User updateUser(String firstName, String lastName, User user) {
        LOGGER.info("Transaction Id: {}, Method: {}, Updating user: {}", MDC.get("transactionId"), MDC.get("MethodName"), user.getUsername());
        user.setFirstName((firstName == null || firstName.isEmpty()) ? user.getFirstName() : firstName);
        user.setLastName((lastName == null || lastName.isEmpty()) ? user.getLastName() : lastName);
        saveUser(user);
        LOGGER.info("Successfully updated user: {}", user.getUsername());
        return user;
    }

    @Transactional
    public User saveUser(User user) {
        User savedUser = userRepository.save(user);
        LOGGER.info("Transaction Id: {}, Method: {}, Successfully saved user: {}", MDC.get("transactionId"), MDC.get("MethodName"), savedUser.getUsername());
        return savedUser;
    }

    @Transactional
    public void deleteUser(User user) {
        userRepository.delete(user);
        LOGGER.info("Transaction Id: {}, Method: {}, Successfully deleted user: {}", MDC.get("transactionId"), MDC.get("MethodName"), user.getUsername());
    }

    @Transactional
    public RegisterResponse updatePassword(String username, String newPassword) {
        LOGGER.info("Transaction Id: {}, Method: {}, Updating password for user: {}", MDC.get("transactionId"), MDC.get("MethodName"), username);
        User user = findByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));
        Validations.validatePassword(newPassword);
        user.setPassword(passwordEncoder.encode(newPassword));
        saveUser(user);
        return new RegisterResponse(user.getUsername(), newPassword);
    }

    private String generateUniqueUsername(String firstName, String lastName) {
        LOGGER.info("Transaction Id: {}, Generating unique username for: {} {}", MDC.get("transactionId"), firstName, lastName);
        String baseUsername = firstName + "." + lastName;
        String username = baseUsername;
        int suffix = 1;
        while (findByUsername(username).isPresent()) {
            username = baseUsername + suffix;
            suffix++;
            LOGGER.info("Transaction Id: {}, Username already exists, trying: {}", MDC.get("transactionId"), username);
        }
        LOGGER.info("Transaction Id: {}, Generated unique username: {}", MDC.get("transactionId"), username);
        return username;
    }
}
