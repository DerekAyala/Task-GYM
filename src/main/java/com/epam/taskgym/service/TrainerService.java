package com.epam.taskgym.service;

import com.epam.taskgym.dto.TrainerDTO;
import com.epam.taskgym.entity.Trainer;
import com.epam.taskgym.entity.User;
import com.epam.taskgym.helpers.Validations;
import com.epam.taskgym.repository.TrainerRepository;
import com.epam.taskgym.repository.TrainingRepository;
import com.epam.taskgym.exception.FailAuthenticateException;
import com.epam.taskgym.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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
    public Trainer registerTrainer(TrainerDTO trainerDTO) {
        Validations.validateTrainerDetails(trainerDTO);
        User user = userService.createUser(trainerDTO.getFirstName(), trainerDTO.getLastName());
        Trainer trainer = new Trainer();
        trainer.setUser(user);
        Validations.validateSpecialization(trainerDTO.getSpecialization());
        trainer.setSpecialization(trainingTypeService.getTrainingTypeByName(trainerDTO.getSpecialization()));
        trainer.setTrainees(new ArrayList<>());
        saveTrainer(trainer);
        LOGGER.info("Successfully registered trainer: {}", trainer.getUser().getUsername());
        return trainer;
    }

    @Transactional
    public void saveTrainer(Trainer trainer) {
        trainerRepository.save(trainer);
    }

    @Transactional
    public Trainer updateTrainer(TrainerDTO trainerDTO, String username, String password) {
        authenticateTrainer(username, password);
        Validations.validateTrainerDetails(trainerDTO);
        Trainer trainer = getTrainerByUsername(username);
        System.out.println(trainer);
        User user = userService.updateUser(trainerDTO.getFirstName(), trainerDTO.getLastName(), trainer.getUser());
        trainer.setUser(user);
        Validations.validateSpecialization(trainerDTO.getSpecialization());
        trainer.setSpecialization(trainingTypeService.getTrainingTypeByName(trainerDTO.getSpecialization()));
        saveTrainer(trainer);
        LOGGER.info("Trainer updated: {}", trainer.getUser().getUsername());
        return trainer;
    }

    @Transactional
    public User ActivateDeactivateTrainer(String username, String password, boolean isActive) {
        LOGGER.info("Activating/Deactivating trainer: {}", username);
        authenticateTrainer(username, password);
        Trainer trainer = getTrainerByUsername(username);
        User user = trainer.getUser();
        user.setIsActive(isActive);
        userService.saveUser(user);
        LOGGER.info("Trainer {} isActive: {}", username, user.getIsActive());
        return user;
    }

    public List<Trainer> getUnassignedTrainers(String traineeUsername) {
        List<Trainer> allTrainers = trainerRepository.findAll();
        List<Trainer> trainersAssignedToTrainee = trainingRepository.findAllTrainersByTraineeUsername(traineeUsername);
        allTrainers.removeAll(trainersAssignedToTrainee);
        return allTrainers;
    }
}
