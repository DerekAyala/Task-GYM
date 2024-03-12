package com.epam.taskgym.service;

import com.epam.taskgym.entity.User;
import com.epam.taskgym.exception.NotFoundException;
import com.epam.taskgym.helpers.Builders;
import com.epam.taskgym.helpers.Validations;
import com.epam.taskgym.models.UserResponse;
import com.epam.taskgym.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
        LOGGER.info("Finding user by username: {}", username);
        return userRepository.findByUsername(username);
    }

    @Transactional
    public UserResponse createUser(String firstName, String lastName, String role) {
        LOGGER.info("Creating user with: {}, {}", firstName, lastName);
        Validations.validateUserDetails(firstName, lastName);
        String username = generateUniqueUsername(firstName.toLowerCase(), lastName.toLowerCase());
        String password = Builders.generateRandomPassword();
        User user = Builders.buildUser(firstName, lastName, username, passwordEncoder.encode(password), role);
        saveUser(user);
        LOGGER.info("Successfully created user: {}", user);
        return new UserResponse(user,password);
    }

    @Transactional
    public User updateUser(String firstName, String lastName, User user) {
        LOGGER.info("Updating user: {}", user);
        user.setFirstName((firstName == null || firstName.isEmpty()) ? user.getFirstName() : firstName);
        user.setLastName((lastName == null || lastName.isEmpty()) ? user.getLastName() : lastName);
        saveUser(user);
        LOGGER.info("Successfully updated user: {}", user);
        return user;
    }

    @Transactional
    public User saveUser(User user) {
        LOGGER.info("Saving user: {}", user);
        User savedUser = userRepository.save(user);
        LOGGER.info("Successfully saved user: {}", savedUser);
        return savedUser;
    }

    @Transactional
    public void deleteUser(User user) {
        LOGGER.info("Deleting user: {}", user);
        userRepository.delete(user);
        LOGGER.info("Successfully deleted user: {}", user);
    }

    @Transactional
    public User updatePassword(String username, String newPassword) {
        LOGGER.info("Updating password for user with username: {}", username);
        User user = findByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));
        Validations.validatePassword(newPassword);
        user.setPassword(passwordEncoder.encode(newPassword));
        return saveUser(user);
    }

    private String generateUniqueUsername(String firstName, String lastName) {
        LOGGER.info("Generating unique username for: {} {}", firstName, lastName);
        String baseUsername = firstName + "." + lastName;
        String username = baseUsername;
        int suffix = 1;
        while (findByUsername(username).isPresent()) {
            username = baseUsername + suffix;
            suffix++;
            LOGGER.debug("Username already exists, generating a new one: {}", username);
        }
        LOGGER.info("Unique username generated successfully: {}", username);
        return username;
    }
}
