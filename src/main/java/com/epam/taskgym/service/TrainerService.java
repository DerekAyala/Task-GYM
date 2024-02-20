package com.epam.taskgym.service;

import com.epam.taskgym.entity.Trainer;
import com.epam.taskgym.entity.TrainingType;
import com.epam.taskgym.entity.User;
import com.epam.taskgym.repository.TrainerRepository;
import com.epam.taskgym.repository.TrainingRepository;
import com.epam.taskgym.exception.FailAuthenticateException;
import com.epam.taskgym.exception.MissingAttributes;
import com.epam.taskgym.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class TrainerService {

    @Autowired
    private TrainerRepository trainerRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private TrainingTypeService trainingTypeService;
    @Autowired
    private TrainingRepository trainingRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerService.class);


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
    public Trainer registerTrainer(Map<String, String> trainerDetails) {
        validateTrainerDetails(trainerDetails);
        User user = userService.createUser(trainerDetails);
        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(validateSpecialization(trainerDetails));
        trainerRepository.save(trainer);
        LOGGER.info("Successfully registered trainer: {}", trainer);
        return trainer;
    }

    @Transactional
    public Trainer updateTrainer(Map<String, String> trainerDetails, String username, String password) {
        authenticateTrainer(username, password);
        validateTrainerDetails(trainerDetails);
        Trainer trainer = getTrainerByUsername(username);
        User user = userService.updateUser(trainerDetails, trainer.getUser());
        trainer.setUser(user);
        trainer.setSpecialization(validateSpecialization(trainerDetails));
        trainerRepository.save(trainer);
        LOGGER.info("Trainer updated: {}", trainer);
        return trainer;
    }

    @Transactional
    public void updatePasssword(String username, String password, String newPassword) {
        authenticateTrainer(username, password);
        TraineeService.validatePassword(newPassword);
        Trainer trainer = getTrainerByUsername(username);
        User user = trainer.getUser();
        user.setPassword(newPassword);
        userService.saveUser(user);
        trainer.setUser(user);
        trainerRepository.save(trainer);
        LOGGER.info("Password updated for trainer: {}", trainer);
    }

    public List<Trainer> getUnassignedTrainers(String traineeUsername) {
        List<Trainer> allTrainers = trainerRepository.findAll();
        List<Trainer> trainersAssignedToTrainee = trainingRepository.findAllTrainersByTraineeUsername(traineeUsername);

        allTrainers.removeAll(trainersAssignedToTrainee);

        return allTrainers;
    }

    private TrainingType validateSpecialization(Map<String, String> trainerDetails) {
        if ((!trainerDetails.containsKey("specialization") || trainerDetails.get("specialization").isEmpty())) {
            LOGGER.error("specialization is required");
            throw new MissingAttributes("specialization is required");
        }
        return trainingTypeService.getTrainingTypeByName(trainerDetails.get("specialization"));

    }

    private void validateTrainerDetails(Map<String, String> trainerDetails) {
        LOGGER.info("Validating trainer details: {}", trainerDetails);
        if (trainerDetails == null || trainerDetails.isEmpty()) {
            LOGGER.error("Trainer details cannot be null or empty");
            throw new MissingAttributes("Trainer details cannot be null or empty");
        }
    }
}
