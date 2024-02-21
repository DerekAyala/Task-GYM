package com.epam.taskgym.service;

import com.epam.taskgym.controller.helpers.TrainerDetails;
import com.epam.taskgym.entity.Trainer;
import com.epam.taskgym.entity.TrainingType;
import com.epam.taskgym.entity.User;
import com.epam.taskgym.repository.TrainerRepository;
import com.epam.taskgym.repository.TrainingRepository;
import com.epam.taskgym.exception.FailAuthenticateException;
import com.epam.taskgym.exception.MissingAttributes;
import com.epam.taskgym.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;


@Service
public class TrainerService {

    private final TrainerRepository trainerRepository;
    private final UserService userService;
    private final TrainingTypeService trainingTypeService;
    private final TrainingRepository trainingRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerService.class);

    public TrainerService(TrainerRepository trainerRepository, UserService userService, TrainingTypeService trainingTypeService, TrainingRepository trainingRepository) {
        this.trainerRepository = trainerRepository;
        this.userService = userService;
        this.trainingTypeService = trainingTypeService;
        this.trainingRepository = trainingRepository;
    }

    private void authenticateTrainer(String username, String password) {
        User user = userService.authenticateUser(username, password);
        Trainer trainer = getTrainerByUsername(username);
        if (trainer.getUser().equals(user)) {
            LOGGER.info("Trainer authenticated: {}", username);
        } else {
            LOGGER.error("Fail to authenticate: Trainer and user do not match");
            throw new FailAuthenticateException("Fail to authenticate: Trainer and user do not match");
        }
    }

    public Trainer getTrainerByUsername(String username) {
        LOGGER.info("Finding trainer by username: {}", username);
        Optional<Trainer> trainer = trainerRepository.findByUserUsername(username);
        if (trainer.isEmpty()) {
            LOGGER.error("Trainer with username {} not found", username);
            throw new NotFoundException("Trainer with username {" + username + "} not found");
        }
        return trainer.get();
    }

    @Transactional
    public Trainer registerTrainer(TrainerDetails trainerDetails) {
        validateTrainerDetails(trainerDetails);
        validateAttributes(trainerDetails.getFirstName(), trainerDetails.getLastName());
        User user = userService.createUser(trainerDetails.getFirstName(), trainerDetails.getLastName());
        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(validateSpecialization(trainerDetails.getSpecialization()));
        trainerRepository.save(trainer);
        LOGGER.info("Successfully registered trainer: {}", trainer);
        return trainer;
    }

    @Transactional
    public Trainer updateTrainer(TrainerDetails trainerDetails, String username, String password) {
        authenticateTrainer(username, password);
        validateTrainerDetails(trainerDetails);
        Trainer trainer = getTrainerByUsername(username);
        System.out.println(trainer);
        User user = userService.updateUser(trainerDetails.getFirstName(), trainerDetails.getLastName(), trainer.getUser());
        trainer.setUser(user);
        trainer.setSpecialization(validateSpecialization(trainerDetails.getSpecialization()));
        trainerRepository.save(trainer);
        LOGGER.info("Trainer updated: {}", trainer);
        return trainer;
    }

    public List<Trainer> getUnassignedTrainers(String traineeUsername) {
        List<Trainer> allTrainers = trainerRepository.findAll();
        List<Trainer> trainersAssignedToTrainee = trainingRepository.findAllTrainersByTraineeUsername(traineeUsername);

        allTrainers.removeAll(trainersAssignedToTrainee);

        return allTrainers;
    }

    private TrainingType validateSpecialization(String specialization) {
        if (specialization == null || specialization.isEmpty()) {
            LOGGER.error("specialization is required");
            throw new MissingAttributes("specialization is required");
        }
        return trainingTypeService.getTrainingTypeByName(specialization);

    }

    private void validateTrainerDetails(TrainerDetails trainerDetails) {
        LOGGER.info("Validating trainer details: {}", trainerDetails);
        if (trainerDetails == null) {
            LOGGER.error("Trainer details cannot be null or empty");
            throw new MissingAttributes("Trainer details cannot be null or empty");
        }
    }

    private void validateAttributes(String firstName, String lastName) {
        LOGGER.info("Validating attributes");
        if (firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty()) {
            LOGGER.error("First name and last name are required");
            throw new MissingAttributes("First name and last name are required");
        }
    }
}
