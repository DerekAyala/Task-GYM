package com.epam.taskgym.helpers;

import com.epam.taskgym.models.TraineeDTO;
import com.epam.taskgym.models.TrainerDTO;
import com.epam.taskgym.models.TrainingDTO;
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

    public static void validateTrainerDetails(TrainerDTO trainerDTO) {
        LOGGER.info("Validating trainer details: {}", trainerDTO);
        if (trainerDTO == null) {
            LOGGER.error("Trainer details cannot be null or empty");
            throw new MissingAttributes("Trainer details cannot be null or empty");
        }
    }

    public static void validateTrainingTypeDetails(String name) {
        LOGGER.info("Validating training type details: {}", name);
        if (name == null || name.isEmpty()) {
            LOGGER.error("Name is required");
            throw new MissingAttributes("Name is required");
        }
    }

    public static void validateTrainingDetails(TrainingDTO trainingDTO) {
        LOGGER.info("Validating training details: {}", trainingDTO);
        if (trainingDTO == null) {
            LOGGER.error("Training details are required");
            throw new MissingAttributes("Training details are required");
        }
        if (trainingDTO.getTraineeUsername() == null || trainingDTO.getTraineeUsername().isEmpty() ||
                trainingDTO.getTrainerUsername() == null || trainingDTO.getTrainerUsername().isEmpty() ||
                trainingDTO.getDate() == null ||
                trainingDTO.getName() == null || trainingDTO.getName().isEmpty() ||
                trainingDTO.getDuration() <= 0) {
            LOGGER.error("Trainee username, trainer username, date, training type name, name and duration are required");
            throw new MissingAttributes("Trainee username, trainer username, date, training type name, name and duration are required");
        }
    }

    public static void validateSpecialization(String specialization) {
        if (specialization == null || specialization.isEmpty()) {
            LOGGER.error("specialization is required");
            throw new MissingAttributes("specialization is required");
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

    public static void validateUsername(String username) {
        LOGGER.info("Validating username: {}", username);
        if (username == null || username.isEmpty()) {
            LOGGER.error("Username is required");
            throw new MissingAttributes("Username is required");
        }
    }

    public static Integer validateDuration(String duration) {
        LOGGER.info("Validating duration: {}", duration);
        try {
            return Integer.parseInt(duration);
        } catch (NumberFormatException e) {
            LOGGER.error("Duration must be a number: " + e);
            throw new BadRequestException("Duration must be a number");
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
