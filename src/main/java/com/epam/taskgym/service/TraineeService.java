package com.epam.taskgym.service;

import com.epam.taskgym.entity.Training;
import com.epam.taskgym.models.*;
import com.epam.taskgym.entity.Trainee;
import com.epam.taskgym.entity.Trainer;
import com.epam.taskgym.entity.User;
import com.epam.taskgym.exception.*;
import com.epam.taskgym.helpers.Builders;
import com.epam.taskgym.helpers.Validations;
import com.epam.taskgym.repository.TraineeRepository;
import com.epam.taskgym.repository.TrainerRepository;
import com.epam.taskgym.repository.TrainingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TraineeService {

    private final TraineeRepository traineeRepository;
    private final UserService userService;
    private final TrainingRepository trainingRepository;
    private final TrainerRepository trainerRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeService.class);

    public Trainee getTraineeByUsername(String username) {
        Validations.validateUsername(username);
        LOGGER.info("Transaction Id: {}, Method: {}, Finding trainee by username: {}", MDC.get("transactionId"), MDC.get("MethodName"), username);
        Optional<Trainee> trainee = traineeRepository.findByUserUsername(username);
        if (trainee.isEmpty()) {
            LOGGER.error("Transaction Id: {}, Method: {}, Trainee with username {} not found", MDC.get("transactionId"), MDC.get("MethodName"), username);
            throw new NotFoundException("Trainee with username {" + username + "} not found");
        }
        return trainee.get();
    }

    @Transactional
    public void saveTrainee(Trainee trainee) {
        traineeRepository.save(trainee);
    }

    @Transactional
    public RegisterResponse registerTrainee(TraineeDTO traineeDTO) {
        Validations.validateTraineeDetails(traineeDTO);
        LOGGER.info("Transaction Id: {}, Method: {}, Registering trainee: {} {}", MDC.get("transactionId"), MDC.get("MethodName"), traineeDTO.getFirstName(), traineeDTO.getLastName());
        UserResponse user = userService.createUser(traineeDTO.getFirstName(), traineeDTO.getLastName(), "ROLE_TRAINEE");
        Trainee trainee = new Trainee();
        trainee.setUser(user.getUser());
        addDate(traineeDTO.getDateOfBirth(), trainee);
        trainee.setAddress((traineeDTO.getAddress() == null || traineeDTO.getAddress().isEmpty()) ? null : traineeDTO.getAddress());
        trainee.setTrainers(new ArrayList<>());
        saveTrainee(trainee);
        LOGGER.info("Transaction Id: {}, Successfully registered trainee: {}", MDC.get("transactionId"), user.getUser().getUsername());
        return new RegisterResponse(user.getUser().getUsername(), user.getPassword());
    }

    @Transactional
    public Trainee updateTrainee(TraineeDTO traineeDTO, String username) {
        Validations.validateTraineeDetails(traineeDTO);
        Trainee trainee = getTraineeByUsername(username);
        LOGGER.info("Transaction Id: {}, Method: {}, Updating trainee: {}", MDC.get("transactionId"), MDC.get("MethodName"), username);
        User user = userService.updateUser(traineeDTO.getFirstName(), traineeDTO.getLastName(), trainee.getUser());
        addDate(traineeDTO.getDateOfBirth(), trainee);
        trainee.setAddress((traineeDTO.getAddress() == null || traineeDTO.getAddress().isEmpty()) ? null : traineeDTO.getAddress());
        saveTrainee(trainee);
        LOGGER.info("Transaction Id: {}, Successfully updated trainee: {}", MDC.get("transactionId"), user.getUsername());
        return trainee;
    }

    @Transactional
    public void deleteTrainee(String username) {
        Trainee trainee = getTraineeByUsername(username);
        LOGGER.info("Transaction Id: {}, Method: {}, Deleting trainee: {}", MDC.get("transactionId"), MDC.get("MethodName"), username);
        try {
            List<Trainer> trainersAssignedToTrainee = trainingRepository.findAllTrainersByTraineeUsername(username);
            trainersAssignedToTrainee.forEach(trainer -> {
                trainer.getTrainees().remove(trainee);
                trainerRepository.save(trainer);
            });
            List<Training> trainings = trainingRepository.findAllByTrainee_User_Username(username);
            trainings.forEach(training -> {
                TrainingRequest trainingRequest = TrainingRequest.builder()
                        .username(training.getTrainer().getUser().getUsername())
                        .firstName(training.getTrainer().getUser().getFirstName())
                        .lastName(training.getTrainer().getUser().getLastName())
                        .isActive(training.getTrainer().getUser().getIsActive())
                        .date(training.getDate())
                        .duration(training.getDuration())
                        .action("delete")
                        .build();
                //microserviceClient.actionTraining(trainingRequest, MDC.get("transactionId"), MDC.get("Authorization"));
            });
            trainingRepository.deleteAll(trainings);
            LOGGER.info("Transaction Id: {}, Trainings deleted for trainee: {}", MDC.get("transactionId"), username);
            User user = trainee.getUser();
            traineeRepository.delete(trainee);
            LOGGER.info("Transaction Id: {}, Successfully deleted trainee: {}", MDC.get("transactionId"), user.getUsername());
            userService.deleteUser(user);
        } catch (DataAccessException e) {
            LOGGER.error("Transaction Id: {}, Method: {}, An error occurred while deleting trainee with username: {}", MDC.get("transactionId"), MDC.get("MethodName"), username, e);
            throw new TraineeDeletionException("An error occurred while deleting trainee with username: " + username + " " + e.getMessage(), e);
        }
    }

    @Transactional
    public TraineeDTO ActivateDeactivateTrainee(String username, boolean isActive) {
        LOGGER.info("Transaction Id: {}, Method: {}, Activating/Deactivating trainee: {}", MDC.get("transactionId"), MDC.get("MethodName"), username);
        Trainee trainee = getTraineeByUsername(username);
        User user = trainee.getUser();
        user.setIsActive(isActive);
        userService.saveUser(user);
        LOGGER.info("Transaction Id: {}, Successfully activated/deactivated trainee: {}", MDC.get("transactionId"), user.getUsername());
        return Builders.convertTraineeToTraineeDTO(trainee);
    }

    @Transactional
    public List<TrainerListItem> updateTrainersList(String username, List<String> trainersUsernames) {
        LOGGER.info("Transaction Id: {}, Method: {}, Updating trainers list for trainee: {}", MDC.get("transactionId"), MDC.get("MethodName"), username);
        Validations.validateList(trainersUsernames);
        Trainee trainee = getTraineeByUsername(username);
        List<Trainer> trainers = trainee.getTrainers();
        trainersUsernames.forEach(trainerUsername -> {
            Trainer trainer = trainerRepository.findByUserUsername(trainerUsername).orElseThrow(() -> new NotFoundException("Trainer with username {" + trainerUsername + "} not found"));
            if (!trainers.contains(trainer)) {
                LOGGER.info("Transaction Id: {}, Adding trainer: {} to trainee: {}", MDC.get("transactionId"), trainerUsername, username);
                trainers.add(trainer);
                trainer.getTrainees().add(trainee);
                LOGGER.info("Transaction Id: {}, Successfully added trainer: {} to trainee: {}", MDC.get("transactionId"), trainerUsername, username);
                trainerRepository.save(trainer);
            }
        });
        trainee.setTrainers(trainers);
        saveTrainee(trainee);
        LOGGER.info("Transaction Id: {}, Successfully updated trainers list for trainee: {}", MDC.get("transactionId"), username);
        return Builders.convertTrainersToTrainerListItem(trainers);
    }

    private void addDate(Date dateOfBirth, Trainee trainee) {
        if (dateOfBirth != null) {
            trainee.setDateOfBirth(dateOfBirth);
            LOGGER.info("Transaction Id: {}, Successfully added date of birth for trainee: {}", MDC.get("transactionId"), trainee.getUser().getUsername());
        }
    }
}
