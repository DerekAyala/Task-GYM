package com.epam.taskgym.helpers;

import com.epam.taskgym.dto.TraineeDTO;
import com.epam.taskgym.exception.BadRequestException;
import com.epam.taskgym.exception.InvalidPasswordException;
import com.epam.taskgym.exception.MissingAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Validations {
    private static final Logger LOGGER = LoggerFactory.getLogger(Validations.class);

    public static void validateUserDetails(String firstName, String lastName) {
        LOGGER.info("Validating user details");
        if (firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty()) {
            LOGGER.error("First name and last name are required.");
            throw new MissingAttributes("First name and last name are required.");
        }
    }

    public static void validateTraineeDetails(TraineeDTO traineeDTO) {
        LOGGER.info("Validating trainee details is not null: {}", traineeDTO);
        if (traineeDTO == null){
            LOGGER.error("Trainee details cannot be null");
            throw new MissingAttributes("Trainee details cannot be null");
        }
    }

    public static Date validateDate(String StringDate) {
        LOGGER.info("Validating date: {}", StringDate);
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        Date date;
        try {
            date = df.parse(StringDate);
        } catch (ParseException e) {
            LOGGER.error("Invalid date format {DD-MM-YYYY}");
            throw new BadRequestException("Invalid date format {DD-MM-YYYY}");
        }
        return date;
    }

    public static void validateList(List<?> list) {
        if (list == null || list.isEmpty()) {
            LOGGER.error("List cannot be null or empty");
            throw new MissingAttributes("List cannot be null or empty");
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
