package com.epam.taskgym.service;

import com.epam.taskgym.controller.helpers.TraineeDetails;
import com.epam.taskgym.entity.Trainee;
import com.epam.taskgym.entity.Trainer;
import com.epam.taskgym.entity.User;
import com.epam.taskgym.exception.*;
import com.epam.taskgym.repository.TraineeRepository;
import com.epam.taskgym.repository.TrainerRepository;
import com.epam.taskgym.repository.TrainingRepository;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TraineeService {

    private final TraineeRepository traineeRepository;
    private final UserService userService;
    private final TrainingRepository trainingRepository;
    private final TrainerRepository trainerRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeService.class);

    public TraineeService(TraineeRepository traineeRepository, UserService userService, TrainingRepository trainingRepository, TrainerRepository trainerRepository) {
        this.traineeRepository = traineeRepository;
        this.userService = userService;
        this.trainingRepository = trainingRepository;
        this.trainerRepository = trainerRepository;
    }

    private void authenticateTrainee(String username, String password) {
        User user = userService.authenticateUser(username, password);
        Trainee trainee = getTraineeByUsername(username);
        if (trainee.getUser().equals(user)) {
            LOGGER.info("Trainee authenticated: {}", username);
        } else {
            LOGGER.error("Fail to authenticate: Trainee and user do not match");
            throw new FailAuthenticateException("Fail to authenticate: Trainee and user do not match");
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
    public Trainee registerTrainee(TraineeDetails traineeDetails) {
        validateTraineeDetails(traineeDetails);
        validateAttributes(traineeDetails.getFirstName(), traineeDetails.getLastName());
        User user = userService.createUser(traineeDetails.getFirstName(), traineeDetails.getLastName());
        Trainee trainee = new Trainee();
        trainee.setUser(user);
        addDate(traineeDetails.getDateOfBirth(), trainee);
        trainee.setAddress((traineeDetails.getAddress() == null || traineeDetails.getAddress().isEmpty()) ? "" : traineeDetails.getAddress());
        traineeRepository.save(trainee);
        LOGGER.info("Trainee registered: {}", trainee);
        return trainee;
    }

    @Transactional
    public Trainee updateTrainee(TraineeDetails traineeDetails, String username, String password) {
        authenticateTrainee(username, password);
        validateTraineeDetails(traineeDetails);
        Trainee trainee = getTraineeByUsername(username);
        User user = userService.updateUser(traineeDetails.getFirstName(), traineeDetails.getLastName(), trainee.getUser());
        trainee.setUser(user);
        addDate(traineeDetails.getDateOfBirth(), trainee);
        trainee.setAddress((traineeDetails.getAddress() == null || traineeDetails.getAddress().isEmpty()) ? "" : traineeDetails.getAddress());
        traineeRepository.save(trainee);
        LOGGER.info("Trainee updated: {}", trainee);
        return trainee;
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
        Date date;
        try {
            date = df.parse(StringDate);
        } catch (ParseException e) {
            LOGGER.error("Invalid date format {DD-MM-YYYY}");
            throw new BadRequestException("Invalid date format {DD-MM-YYYY}");
        }
        return date;
    }

    private void addDate(Date dateOfBirth, Trainee trainee) {
        if (dateOfBirth != null) {
            trainee.setDateOfBirth(dateOfBirth);
            LOGGER.info("Date of birth added: {}", dateOfBirth);
        }
    }

    private void validateTraineeDetails(TraineeDetails traineeDetails) {
        LOGGER.info("Validating trainee details is not null: {}", traineeDetails);
        if (traineeDetails == null){
            LOGGER.error("Trainee details cannot be null");
            throw new MissingAttributes("Trainee details cannot be null");
        }
    }

    private void validateAttributes(String firstName, String lastName) {
        LOGGER.info("Validating attributes: {} , {}", firstName, lastName);
        if (firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty()) {
            LOGGER.error("First name and last name are required");
            throw new MissingAttributes("First name and last name are required");
        }
    }
}
