package com.epam.taskgym.service;

import com.epam.taskgym.entity.User;
import com.epam.taskgym.exception.FailAuthenticateException;
import com.epam.taskgym.exception.NotFoundException;
import com.epam.taskgym.helpers.Builders;
import com.epam.taskgym.helpers.Validations;
import com.epam.taskgym.repository.UserRepository;
import com.epam.taskgym.exception.MissingAttributes;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private Optional<User> findByUsername(String username) {
        LOGGER.info("Finding user by username: {}", username);
        return userRepository.findByUsername(username);
    }

    @Transactional
    public User createUser(String firstName, String lastName) {
        LOGGER.info("Creating user with: {}, {}", firstName, lastName);
        Validations.validateUserDetails(firstName, lastName);
        String username = generateUniqueUsername(firstName.toLowerCase(), lastName.toLowerCase());
        String password = Builders.generateRandomPassword();
        User user = Builders.buildUser(firstName, lastName, username, password);
        saveUser(user);
        LOGGER.info("Successfully created user: {}", user);
        return user;
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
    public User updatePassword(String username, String password, String newPassword) {
        LOGGER.info("Updating password for user with username: {}", username);
        User user = authenticateUser(username, password);
        Validations.validatePassword(newPassword);
        user.setPassword(newPassword);
        return saveUser(user);
    }

    public User authenticateUser(String username, String password) {
        LOGGER.info("Authenticating user with username: {}", username);
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            LOGGER.error("Username and password are required");
            throw new MissingAttributes("Username and password are required");
        }
        User user = findByUsername(username).orElseThrow(() -> {
            LOGGER.error("User with username {} not found", username);
            return new NotFoundException("User with username {" + username + "} not found");
        });
        if (!user.getPassword().equals(password)) {
            LOGGER.error("Fail to authenticate: Password and username do not match");
            throw new FailAuthenticateException("Fail to authenticate: Password and username do not match");
        }
        return user;
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
