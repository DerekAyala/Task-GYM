package com.epam.taskgym.service;

import com.epam.taskgym.entity.User;
import com.epam.taskgym.repository.UserRepository;
import com.epam.taskgym.service.exception.MissingAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Repository
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByUsernameAndPassword(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password);
    }

    public String generateRandomPassword() {
        return new Random().ints(48, 122)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .mapToObj(i -> (char) i)
                .limit(10)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    public String generateUniqueUsername(String firstName, String lastName) {
        String baseUsername = firstName + "." + lastName;
        String username = baseUsername;

        int suffix = 1;

        while (findByUsername(username).isPresent()) {
            username = baseUsername + suffix;
            suffix++;
        }

        return username;
    }

    public User createUser(Map<String, String> userDetails) {
        if ((!userDetails.containsKey("firstName") || userDetails.get("firstName").isEmpty()) ||
                (!userDetails.containsKey("lastName") || userDetails.get("lastName").isEmpty())) {
            throw new MissingAttributes("First name and lastName are required");
        }
        String firstName = userDetails.get("firstName");
        String lastName = userDetails.get("lastName");
        String username = generateUniqueUsername(firstName.toLowerCase(), lastName.toLowerCase());
        String password = generateRandomPassword();

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(password);
        userRepository.save(user);

        return user;
    }

    public User updateUser(Map<String, String> userDetails, User user) {
        user.setFirstName(userDetails.getOrDefault("firstName", user.getFirstName()));
        user.setLastName(userDetails.getOrDefault("lastName", user.getLastName()));
        userRepository.save(user);

        return user;
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User deActivateUser(User user) {
        user.setIsActive(false);
        userRepository.save(user);
        return user;
    }

    public User activateUser(User user) {
        user.setIsActive(true);
        userRepository.save(user);
        return user;
    }
}
