package com.epam.taskgym.service;

import com.epam.taskgym.models.TrainerDTO;
import com.epam.taskgym.models.TrainerListItem;
import com.epam.taskgym.entity.Trainer;
import com.epam.taskgym.entity.User;
import com.epam.taskgym.helpers.Builders;
import com.epam.taskgym.helpers.Validations;
import com.epam.taskgym.repository.TrainerRepository;
import com.epam.taskgym.repository.TrainingRepository;
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
        User user = userService.createUser(trainerDTO.getFirstName(), trainerDTO.getLastName(), "ROLE_TRAINER");
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
    public Trainer updateTrainer(TrainerDTO trainerDTO, String username) {
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
    public TrainerDTO ActivateDeactivateTrainer(String username, boolean isActive) {
        LOGGER.info("Activating/Deactivating trainer: {}", username);
        Trainer trainer = getTrainerByUsername(username);
        User user = trainer.getUser();
        user.setIsActive(isActive);
        userService.saveUser(user);
        trainer.setUser(user);
        LOGGER.info("Trainer {} isActive: {}", username, user.getIsActive());
        return Builders.convertTrainerToTraineeDTO(trainer);
    }

    public List<TrainerListItem> getUnassignedTrainers(String traineeUsername) {
        List<Trainer> allTrainers = trainerRepository.findAll();
        List<Trainer> trainersAssignedToTrainee = trainingRepository.findAllTrainersByTraineeUsername(traineeUsername);
        allTrainers.removeAll(trainersAssignedToTrainee);
        return Builders.convertTrainersToTrainerListItem(allTrainers);
    }
}
