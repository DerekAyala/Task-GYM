package com.epam.taskgym.service;

import com.epam.taskgym.models.RegisterResponse;
import com.epam.taskgym.models.TrainerDTO;
import com.epam.taskgym.models.TrainerListItem;
import com.epam.taskgym.entity.Trainer;
import com.epam.taskgym.entity.User;
import com.epam.taskgym.helpers.Builders;
import com.epam.taskgym.helpers.Validations;
import com.epam.taskgym.models.UserResponse;
import com.epam.taskgym.repository.TrainerRepository;
import com.epam.taskgym.repository.TrainingRepository;
import com.epam.taskgym.exception.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class TrainerService {

    private final TrainerRepository trainerRepository;
    private final UserService userService;
    private final TrainingTypeService trainingTypeService;
    private final TrainingRepository trainingRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerService.class);

    public Trainer getTrainerByUsername(String username) {
        LOGGER.info("Transaction Id: {}, Method: {}, Finding trainer by username: {}", MDC.get("transactionId"), MDC.get("MethodName"), username);
        Optional<Trainer> trainer = trainerRepository.findByUserUsername(username);
        if (trainer.isEmpty()) {
            LOGGER.error("Transaction Id: {}, Method: {}, Trainer with username {} not found", MDC.get("transactionId"), MDC.get("MethodName"), username);
            throw new NotFoundException("Trainer with username {" + username + "} not found");
        }
        return trainer.get();
    }

    @Transactional
    public RegisterResponse registerTrainer(TrainerDTO trainerDTO) {
        Validations.validateTrainerDetails(trainerDTO);
        LOGGER.info("Transaction Id: {}, Method: {}, Registering trainer: {} {}", MDC.get("transactionId"), MDC.get("MethodName"), trainerDTO.getFirstName(), trainerDTO.getLastName());
        UserResponse user = userService.createUser(trainerDTO.getFirstName(), trainerDTO.getLastName(), "ROLE_TRAINER");
        Trainer trainer = new Trainer();
        trainer.setUser(user.getUser());
        Validations.validateSpecialization(trainerDTO.getSpecialization());
        trainer.setSpecialization(trainingTypeService.getTrainingTypeByName(trainerDTO.getSpecialization()));
        trainer.setTrainees(new ArrayList<>());
        saveTrainer(trainer);
        LOGGER.info("Transaction Id: {}, Successfully registered trainer: {}", MDC.get("transactionId"), user.getUser().getUsername());
        return new RegisterResponse(user.getUser().getUsername(), user.getPassword());
    }

    @Transactional
    public void saveTrainer(Trainer trainer) {
        trainerRepository.save(trainer);
    }

    @Transactional
    public Trainer updateTrainer(TrainerDTO trainerDTO, String username) {
        Validations.validateTrainerDetails(trainerDTO);
        LOGGER.info("Transaction Id: {}, Method: {}, Updating trainer: {}", MDC.get("transactionId"), MDC.get("MethodName"), username);
        Trainer trainer = getTrainerByUsername(username);
        System.out.println(trainer);
        User user = userService.updateUser(trainerDTO.getFirstName(), trainerDTO.getLastName(), trainer.getUser());
        Validations.validateSpecialization(trainerDTO.getSpecialization());
        trainer.setSpecialization(trainingTypeService.getTrainingTypeByName(trainerDTO.getSpecialization()));
        saveTrainer(trainer);
        LOGGER.info("Transaction Id: {}, Successfully updated trainer: {}", MDC.get("transactionId"), user.getUsername());
        return trainer;
    }

    @Transactional
    public TrainerDTO ActivateDeactivateTrainer(String username, boolean isActive) {
        LOGGER.info("Transaction Id: {}, Method: {}, Activating/Deactivating trainer: {}", MDC.get("transactionId"), MDC.get("MethodName"), username);
        Trainer trainer = getTrainerByUsername(username);
        User user = trainer.getUser();
        user.setIsActive(isActive);
        userService.saveUser(user);
        LOGGER.info("Transaction Id: {}, Successfully activated/deactivated trainer: {}", MDC.get("transactionId"), user.getUsername());
        return Builders.convertTrainerToTraineeDTO(trainer);
    }

    public List<TrainerListItem> getUnassignedTrainers(String traineeUsername) {
        LOGGER.info("Transaction Id: {}, Method: {}, Finding unassigned trainers for trainee: {}", MDC.get("transactionId"), MDC.get("MethodName"), traineeUsername);
        List<Trainer> allTrainers = trainerRepository.findAll();
        List<Trainer> trainersAssignedToTrainee = trainingRepository.findAllTrainersByTraineeUsername(traineeUsername);
        allTrainers.removeAll(trainersAssignedToTrainee);
        return Builders.convertTrainersToTrainerListItem(allTrainers);
    }
}
