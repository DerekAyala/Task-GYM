package com.epam.taskgym.helpers;

import com.epam.taskgym.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class Builders {
    private static final Logger LOGGER = LoggerFactory.getLogger(Builders.class);
    public static User buildUser(String firstName, String lastName, String username, String password) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(password);
        user.setIsActive(true);
        LOGGER.info("User successfully built: {}", username);
        return user;
    }

    public static String generateRandomPassword() {
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
}
