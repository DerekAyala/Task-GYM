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
    public User createUser(String firstName, String lastName) {
        LOGGER.info("Creating user with: {}, {}", firstName, lastName);
        validateUserDetails(firstName, lastName);
        String username = generateUniqueUsername(firstName.toLowerCase(), lastName.toLowerCase());
        String password = generateRandomPassword();

        User user = buildUser(firstName, lastName, username, password);
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
        validatePassword(newPassword);
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

    private void validateUserDetails(String firstName, String lastName) {
        LOGGER.info("Validating user details: {} , {}", firstName, lastName);
        if (firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty()) {
            LOGGER.error("First name and last name are required");
            throw new MissingAttributes("First name and last name are required");
        }
    }

    private User buildUser(String firstName, String lastName, String username, String password) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
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
