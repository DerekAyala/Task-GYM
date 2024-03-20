package com.epam.taskgym.helpers;

import com.epam.taskgym.models.TraineeDTO;
import com.epam.taskgym.models.TrainerDTO;
import com.epam.taskgym.models.TrainingDTO;
import com.epam.taskgym.exception.BadRequestException;
import com.epam.taskgym.exception.InvalidPasswordException;
import com.epam.taskgym.exception.MissingAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Validations {
    private static final Logger LOGGER = LoggerFactory.getLogger(Validations.class);

    public static void validateUserDetails(String firstName, String lastName) {
        LOGGER.info("Transaction Id: {}, validating user details: {} {}", MDC.get("transactionId"),firstName, lastName);
        if (firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty()) {
            LOGGER.error("Transaction Id: {}, First name and last name are required.", MDC.get("transactionId"));
            throw new MissingAttributes("First name and last name are required.");
        }
    }

    public static void validateTraineeDetails(TraineeDTO traineeDTO) {
        LOGGER.info("Transaction Id: {}, validating trainee details: {}", MDC.get("transactionId"), traineeDTO);
        if (traineeDTO == null){
            LOGGER.error("Transaction Id: {}, Trainee details cannot be null", MDC.get("transactionId"));
            throw new MissingAttributes("Trainee details cannot be null");
        }
    }

    public static void validateTrainerDetails(TrainerDTO trainerDTO) {
        LOGGER.info("Transaction Id: {}, validating trainer details: {}", MDC.get("transactionId"), trainerDTO);
        if (trainerDTO == null) {
            LOGGER.error("Transaction Id: {}, Trainer details cannot be null", MDC.get("transactionId"));
            throw new MissingAttributes("Trainer details cannot be null or empty");
        }
    }

    public static void validateTrainingTypeDetails(String name) {
        LOGGER.info("Transaction Id: {}, validating training type details: {}", MDC.get("transactionId"), name);
        if (name == null || name.isEmpty()) {
            LOGGER.error("Transaction Id: {}, Name is required", MDC.get("transactionId"));
            throw new MissingAttributes("Name is required");
        }
    }

    public static void validateTrainingDetails(TrainingDTO trainingDTO) {
        LOGGER.info("Transaction Id: {}, validating training details: {}", MDC.get("transactionId"), trainingDTO);
        if (trainingDTO == null) {
            LOGGER.error("Transaction Id: {}, Training details are required", MDC.get("transactionId"));
            throw new MissingAttributes("Training details are required");
        }
        if (trainingDTO.getTraineeUsername() == null || trainingDTO.getTraineeUsername().isEmpty() ||
                trainingDTO.getTrainerUsername() == null || trainingDTO.getTrainerUsername().isEmpty() ||
                trainingDTO.getDate() == null ||
                trainingDTO.getName() == null || trainingDTO.getName().isEmpty() ||
                trainingDTO.getDuration() <= 0) {
            LOGGER.error("Transaction Id: {}, Trainee username, trainer username, date, training type name, name and duration are required", MDC.get("transactionId"));
            throw new MissingAttributes("Trainee username, trainer username, date, training type name, name and duration are required");
        }
    }

    public static void validateSpecialization(String specialization) {
        LOGGER.info("Transaction Id: {}, validating specialization: {}", MDC.get("transactionId"), specialization);
        if (specialization == null || specialization.isEmpty()) {
            LOGGER.error("Transaction Id: {}, Specialization is required", MDC.get("transactionId"));
            throw new MissingAttributes("specialization is required");
        }
    }

    public static Date validateDate(String StringDate) {
        LOGGER.info("validating date: {}", StringDate);
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        Date date;
        try {
            date = df.parse(StringDate);
        } catch (ParseException e) {
            LOGGER.error(" Invalid date format {DD-MM-YYYY}");
            throw new BadRequestException("Invalid date format {DD-MM-YYYY}");
        }
        return date;
    }

    public static void validateList(List<?> list) {
        if (list == null || list.isEmpty()) {
            LOGGER.error("Transaction Id: {}, List cannot be null or empty", MDC.get("transactionId"));
            throw new MissingAttributes("List cannot be null or empty");
        }
    }

    public static void validateUsername(String username) {
        LOGGER.info("Transaction Id: {}, validating username: {}", MDC.get("transactionId"), username);
        if (username == null || username.isEmpty()) {
            LOGGER.error("Transaction Id: {}, Username is required", MDC.get("transactionId"));
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
        LOGGER.info("Transaction Id: {}, validating password: {}", MDC.get("transactionId"), password);
        if (password == null || password.isEmpty()) {
            LOGGER.error("Transaction Id: {}, Password cannot be null or empty.", MDC.get("transactionId"));
            throw new InvalidPasswordException("Password cannot be null or empty.");
        }
        if (password.length() < 8) {
            LOGGER.error("Transaction Id: {}, Password must be at least 8 characters long.", MDC.get("transactionId"));
            throw new InvalidPasswordException("Password must be at least 8 characters long.");
        }
    }
}
