package com.epam.taskgym.service;

import com.epam.taskgym.entity.User;
import com.epam.taskgym.repository.UserRepository;
import com.epam.taskgym.service.exception.BadRequestException;
import com.epam.taskgym.service.exception.MissingAttributes;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public User createUser(Map<String, String> userDetails) {
        validateUserDetails(userDetails);
        String username = generateUniqueUsername(userDetails.get("firstName").toLowerCase(), userDetails.get("lastName").toLowerCase());
        String password = generateRandomPassword();

        User user = buildUser(userDetails, username, password);
        saveUser(user);

        return user;
    }

    @Transactional
    public User updateUser(Map<String, String> userDetails, User user) {
        user.setFirstName(userDetails.getOrDefault("firstName", user.getFirstName()));
        user.setLastName(userDetails.getOrDefault("lastName", user.getLastName()));
        saveUser(user);

        return user;
    }

    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public User toggleUserActivation(User user) {
        user.setIsActive(!user.getIsActive());
        saveUser(user);
        return user;
    }

    @Transactional
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    private void validateUserDetails(Map<String, String> userDetails) {
        if ((!userDetails.containsKey("firstName") || userDetails.get("firstName").isEmpty()) ||
                (!userDetails.containsKey("lastName") || userDetails.get("lastName").isEmpty())) {
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
        return user;
    }

    private String generateRandomPassword() {
        return new Random().ints(48, 122)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .mapToObj(i -> (char) i)
                .limit(10)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    private String generateUniqueUsername(String firstName, String lastName) {
        String baseUsername = firstName + "." + lastName;
        String username = baseUsername;

        int suffix = 1;

        while (findByUsername(username).isPresent()) {
            username = baseUsername + suffix;
            suffix++;
        }

        return username;
    }
}
