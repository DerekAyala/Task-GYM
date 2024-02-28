package com.epam.taskgym.helpers;

import com.epam.taskgym.exception.InvalidPasswordException;
import com.epam.taskgym.exception.MissingAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class Validations {
    private static final Logger LOGGER = LoggerFactory.getLogger(Validations.class);

    public static void validateUserDetails(String firstName, String lastName) {
        LOGGER.info("Validating user details");
        if (firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty()) {
            LOGGER.error("First name and last name are required.");
            throw new MissingAttributes("First name and last name are required.");
        }
    }

    public static void validatePassword(String password) {
        LOGGER.info("Validating password");
        if (password == null || password.isEmpty()) {
            LOGGER.error("Password cannot be null or empty.");
            throw new InvalidPasswordException("Password cannot be null or empty.");
        }
        if (password.length() < 8) {
            LOGGER.error("Password must be at least 8 characters long.");
            throw new InvalidPasswordException("Password must be at least 8 characters long.");
        }
    }
}
