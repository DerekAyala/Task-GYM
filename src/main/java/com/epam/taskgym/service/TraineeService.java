package com.epam.taskgym.service;

import com.epam.taskgym.dto.TraineeDTO;
import com.epam.taskgym.dto.TrainerListItem;
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
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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
    public void saveTrainee(Trainee trainee) {
        traineeRepository.save(trainee);
    }

    @Transactional
    public Trainee registerTrainee(TraineeDTO traineeDTO) {
        Validations.validateTraineeDetails(traineeDTO);
        User user = userService.createUser(traineeDTO.getFirstName(), traineeDTO.getLastName());
        Trainee trainee = new Trainee();
        trainee.setUser(user);
        addDate(traineeDTO.getDateOfBirth(), trainee);
        trainee.setAddress((traineeDTO.getAddress() == null || traineeDTO.getAddress().isEmpty()) ? null : traineeDTO.getAddress());
        trainee.setTrainers(new ArrayList<>());
        saveTrainee(trainee);
        LOGGER.info("Trainee registered: {}", trainee.getUser().getUsername());
        return trainee;
    }

    @Transactional
    public Trainee updateTrainee(TraineeDTO traineeDTO, String username) {
        Validations.validateTraineeDetails(traineeDTO);
        Trainee trainee = getTraineeByUsername(username);
        User user = userService.updateUser(traineeDTO.getFirstName(), traineeDTO.getLastName(), trainee.getUser());
        trainee.setUser(user);
        addDate(traineeDTO.getDateOfBirth(), trainee);
        trainee.setAddress((traineeDTO.getAddress() == null || traineeDTO.getAddress().isEmpty()) ? null : traineeDTO.getAddress());
        saveTrainee(trainee);
        LOGGER.info("Trainee updated: {}", trainee.getUser().getUsername());
        return trainee;
    }

    @Transactional
    public void deleteTrainee(String username) {
        Trainee trainee = getTraineeByUsername(username);
        LOGGER.info("Hard Deleting trainee with username: {}", username);
        try {
            List<Trainer> trainersAssignedToTrainee = trainingRepository.findAllTrainersByTraineeUsername(username);
            trainersAssignedToTrainee.forEach(trainer -> {
                trainer.getTrainees().remove(trainee);
                trainerRepository.save(trainer);
            });
            trainingRepository.deleteAllByTrainee_User_Username(username);
            LOGGER.info("Trainings deleted for trainee with username: {}", username);
            User user = trainee.getUser();
            traineeRepository.delete(trainee);
            LOGGER.info("Trainee deleted: {}", trainee.getUser().getUsername());
            userService.deleteUser(user);
        } catch (DataAccessException e) {
            LOGGER.error("An error occurred while deleting trainee with username:" + username, e);
            throw new TraineeDeletionException("An error occurred while deleting trainee with username: " + username + " " + e.getMessage(), e);
        }
    }

    @Transactional
    public TraineeDTO ActivateDeactivateTrainee(String username, boolean isActive) {
        LOGGER.info("Activating/Deactivating trainee: {}", username);
        Trainee trainee = getTraineeByUsername(username);
        User user = trainee.getUser();
        user.setIsActive(isActive);
        userService.saveUser(user);
        trainee.setUser(user);
        LOGGER.info("Trainee {} isActive: {}", username, user.getIsActive());
        return Builders.convertTraineeToTraineeDTO(trainee);
    }

    @Transactional
    public List<TrainerListItem> updateTrainersList(String username, List<String> trainersUsernames) {
        LOGGER.info("Updating trainers list for trainee: {}", username);
        LOGGER.info("Validating trainers list: {}", trainersUsernames);
        Validations.validateList(trainersUsernames);
        Trainee trainee = getTraineeByUsername(username);
        List<Trainer> trainers = trainee.getTrainers();
        trainersUsernames.forEach(trainerUsername -> {
            Trainer trainer = trainerRepository.findByUserUsername(trainerUsername).orElseThrow(() -> new NotFoundException("Trainer with username {" + trainerUsername + "} not found"));
            if (!trainers.contains(trainer)) {
                trainers.add(trainer);
                trainer.getTrainees().add(trainee);
                trainerRepository.save(trainer);
            }
        });
        trainee.setTrainers(trainers);
        saveTrainee(trainee);
        return Builders.convertTrainersToTrainerListItem(trainers);
    }

    private void addDate(Date dateOfBirth, Trainee trainee) {
        if (dateOfBirth != null) {
            trainee.setDateOfBirth(dateOfBirth);
            LOGGER.info("Date of birth added: {}", dateOfBirth);
        }
    }
}
