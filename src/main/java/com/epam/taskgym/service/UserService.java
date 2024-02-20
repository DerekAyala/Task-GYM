package com.epam.taskgym.service;

import com.epam.taskgym.entity.User;
import com.epam.taskgym.exception.FailAuthenticateException;
import com.epam.taskgym.exception.InvalidPasswordException;
import com.epam.taskgym.exception.NotFoundException;
import com.epam.taskgym.repository.UserRepository;
import com.epam.taskgym.exception.MissingAttributes;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {
    private final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    private Optional<User> findByUsername(String username) {
        LOGGER.info("Finding user by username: {}", username);
        return userRepository.findByUsername(username);
    }

    @Transactional
    public User createUser(Map<String, String> userDetails) {
        LOGGER.info("Creating user with details: {}", userDetails);
        validateUserDetails(userDetails);
        String username = generateUniqueUsername(userDetails.get("firstName").toLowerCase(), userDetails.get("lastName").toLowerCase());
        String password = generateRandomPassword();

        User user = buildUser(userDetails, username, password);
        saveUser(user);
        LOGGER.info("Successfully created user: {}", user);

        return user;
    }

    @Transactional
    public User updateUser(Map<String, String> userDetails, User user) {
        LOGGER.info("Updating user: {}", user);
        user.setFirstName(userDetails.getOrDefault("firstName", user.getFirstName()));
        user.setLastName(userDetails.getOrDefault("lastName", user.getLastName()));
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
    public User toggleUserActivation(User user) {
        LOGGER.info("Toggling user activation: {}", user);
        user.setIsActive(!user.getIsActive());
        User updatedUser = saveUser(user);
        LOGGER.info("Successfully updated user activation: {}", updatedUser);
        return updatedUser;
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
        validatePassword(newPassword);
        user.setPassword(newPassword);
        User updatedUser = saveUser(user);
        return user;
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

    private void validatePassword(String newPassword) {
        LOGGER.info("Validating password");
        if(newPassword == null || newPassword.isEmpty()){
            LOGGER.error("Password cannot be null or empty.");
            throw new InvalidPasswordException("Password cannot be null or empty.");
        }

        if(newPassword.length() < 8){
            LOGGER.error("Password must be at least 8 characters long.");
            throw new InvalidPasswordException("Password must be at least 8 characters long.");
        }
    }

    private void validateUserDetails(Map<String, String> userDetails) {
        LOGGER.info("Validating user details: {}", userDetails);
        if ((!userDetails.containsKey("firstName") || userDetails.get("firstName").isEmpty()) ||
                (!userDetails.containsKey("lastName") || userDetails.get("lastName").isEmpty())) {
            LOGGER.error("First name and lastName are missing in user details: {}", userDetails);
            throw new MissingAttributes("First name and lastName are required");
        }
    }

    private User buildUser(Map<String, String> userDetails, String username, String password) {
        User user = new User();
        user.setFirstName(userDetails.get("firstName"));
        user.setLastName(userDetails.get("lastName"));
        user.setUsername(username);
        user.setPassword(password);
        user.setIsActive(true);
        LOGGER.info("User successfully built: {}", username);

        return user;
    }

    private String generateRandomPassword() {
        LOGGER.info("Generating random password");
        String password = new Random().ints(48, 122)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .mapToObj(i -> (char) i)
                .limit(10)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
        LOGGER.info("Random password generated successfully");

        return password;
    }

    private String generateUniqueUsername(String firstName, String lastName) {
        LOGGER.info("Generating unique username for: {} {}", firstName,lastName);
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
