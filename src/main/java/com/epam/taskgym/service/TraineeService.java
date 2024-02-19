package com.epam.taskgym.service;

import com.epam.taskgym.dto.TraineeDTO;
import com.epam.taskgym.entity.Trainee;
import com.epam.taskgym.entity.Trainer;
import com.epam.taskgym.entity.User;
import com.epam.taskgym.exception.*;
import com.epam.taskgym.repository.TraineeRepository;
import com.epam.taskgym.repository.TrainerRepository;
import com.epam.taskgym.repository.TrainingRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TraineeService {

    @Autowired
    private TraineeRepository traineeRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private TrainingRepository trainingRepository;
    @Autowired
    private TrainerRepository trainerRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeService.class);

    private void authenticateTrainee(String username, String password) {
        LOGGER.info("Authenticating trainee with username: {}", username);
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            LOGGER.error("Username and password are required");
            throw new MissingAttributes("Username and password are required");
        }
        Trainee trainee = getTraineeByUsername(username);
        if (!trainee.getUser().getPassword().equals(password)) {
            LOGGER.error("Fail to authenticate: Password and username do not match");
            throw new FailAuthenticateException("Fail to authenticate: Password and username do not match");
        }
    }

    public Trainee getTraineeByUsername(String username) {
        LOGGER.info("Finding trainee by username: {}", username);
        Optional<Trainee> trainee = traineeRepository.findByUserUsername(username);
        if (trainee.isEmpty()) {
            LOGGER.error("Trainee with username {} not found", username);
            throw new NotFoundException("Trainee with username {" + username + "} not found");
        }
        return trainee.get();
    }

    @Transactional
    public TraineeDTO registerTrainee(Map<String, String> traineeDetails) {
        validateTraineeDetails(traineeDetails);
        User user = userService.createUser(traineeDetails);
        Trainee trainee = new Trainee();
        trainee.setUser(user);
        addDate(traineeDetails, trainee);
        trainee.setAddress(traineeDetails.getOrDefault("address", ""));
        traineeRepository.save(trainee);
        LOGGER.info("Trainee registered: {}", trainee);
        return fillTraineeDTO(user, trainee);
    }

    @Transactional
    public TraineeDTO updateTrainee(Map<String, String> traineeDetails, String username, String password) {
        authenticateTrainee(username, password);
        validateTraineeDetails(traineeDetails);
        Trainee trainee = getTraineeByUsername(username);
        User user = userService.updateUser(traineeDetails, trainee.getUser());
        trainee.setUser(user);
        addDate(traineeDetails, trainee);
        trainee.setAddress(traineeDetails.getOrDefault("address", ""));
        traineeRepository.save(trainee);
        LOGGER.info("Trainee updated: {}", trainee);
        return fillTraineeDTO(user, trainee);
    }

    @Transactional
    public void deleteTrainee(String username, String password) {
        authenticateTrainee(username, password);
        Trainee trainee = getTraineeByUsername(username);
        LOGGER.info("Hard Deleting trainee with username: {}", username);
        try {
            trainingRepository.deleteAllByTrainee_User_Username(username);
            LOGGER.info("Trainings deleted for trainee with username: {}", username);
            User user = trainee.getUser();
            traineeRepository.delete(trainee);
            LOGGER.info("Trainee deleted: {}", trainee);
            userService.deleteUser(user);
        } catch (DataAccessException e) {
            LOGGER.error("An error occurred while deleting trainee with username:" + username, e);
            throw new TraineeDeletionException("An error occurred while deleting trainee with username: " + username + " " + e.getMessage(), e);
        }
    }

    @Transactional
    public void updatePasssword(String username, String password, String newPassword) {
        authenticateTrainee(username, password);
        validatePassword(newPassword);
        Trainee trainee = getTraineeByUsername(username);
        User user = trainee.getUser();
        user.setPassword(newPassword);
        userService.saveUser(user);
        trainee.setUser(user);
        traineeRepository.save(trainee);
        LOGGER.info("Password updated for user {}", username);
    }

    public void updateTraineeTrainers(String traineeUsername, List<String> trainerUsernames) {
        Trainee trainee = getTraineeByUsername(traineeUsername);

        List<Trainer> trainers = trainerRepository.findAllByUserUsernameIn(trainerUsernames);

        if(trainers.size() < trainerUsernames.size()) {
            throw new NotFoundException("One or more trainers not found.");
        }

        trainee.setTrainers(trainers);

        traineeRepository.save(trainee);
    }

    public Date validateDate(String StringDate) {
        LOGGER.info("Validating date: {}", StringDate);
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        Date date = null;
        try {
            date = df.parse(StringDate);
        } catch (ParseException e) {
            LOGGER.error("Invalid date format {DD-MM-YYYY}");
            throw new BadRequestException("Invalid date format {DD-MM-YYYY}");
        }
        return date;
    }

    private TraineeDTO fillTraineeDTO(User user, Trainee trainee) {
        TraineeDTO trainerDTO = new TraineeDTO(user.getUsername(), user.getPassword(), user.getFirstName(), user.getLastName(), trainee.getDateOfBirth(), trainee.getAddress());
        LOGGER.info("TraineeDTO filled: {}", trainerDTO);
        return trainerDTO;
    }

    private void addDate(Map<String, String> traineeDetails, Trainee trainee) {
        if (traineeDetails.containsKey("dateOfBirth") && !traineeDetails.get("dateOfBirth").isEmpty()) {
            Date dateOfBirth = validateDate(traineeDetails.get("dateOfBirth"));
            trainee.setDateOfBirth(dateOfBirth);
            LOGGER.info("Date of birth added: {}", dateOfBirth);
        }
    }

    private void validateTraineeDetails(Map<String, String> traineeDetails) {
        LOGGER.info("Validating trainee details: {}", traineeDetails);
        if (traineeDetails == null || traineeDetails.isEmpty()) {
            LOGGER.error("Trainee details cannot be null or empty");
            throw new MissingAttributes("Trainee details cannot be null or empty");
        }
    }

    public static void validatePassword(String newPassword) {
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
}
