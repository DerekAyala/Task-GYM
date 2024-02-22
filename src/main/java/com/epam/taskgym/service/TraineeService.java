package com.epam.taskgym.service;

import com.epam.taskgym.dto.TraineeDTO;
import com.epam.taskgym.dto.TrainerListDTO;
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

    public TraineeDTO convertTraineeToTraineeDTO(Trainee trainee) {
        TraineeDTO traineeDTO = new TraineeDTO();
        traineeDTO.setFirstName(trainee.getUser().getFirstName());
        traineeDTO.setLastName(trainee.getUser().getLastName());
        traineeDTO.setDateOfBirth(trainee.getDateOfBirth());
        traineeDTO.setAddress(trainee.getAddress());
        traineeDTO.setIsActive(trainee.getUser().getIsActive());
        traineeDTO.setTrainers(convertTrainersToTrainerListDTO(trainee.getTrainers()));
        return traineeDTO;
    }

    public ArrayList<TrainerListDTO> convertTrainersToTrainerListDTO(List<Trainer> trainers) {
        ArrayList<TrainerListDTO> trainerListDTO = new ArrayList<>();
        trainers.forEach(trainer -> {
            TrainerListDTO trainerDTO = new TrainerListDTO();
            trainerDTO.setFirstName(trainer.getUser().getFirstName());
            trainerDTO.setLastName(trainer.getUser().getLastName());
            trainerDTO.setUsername(trainer.getUser().getUsername());
            trainerDTO.setSpecialization(trainer.getSpecialization().getName());
            trainerListDTO.add(trainerDTO);
        });
        return trainerListDTO;
    }

    @Transactional
    public void saveTrainee(Trainee trainee) {
        traineeRepository.save(trainee);
    }

    @Transactional
    public Trainee registerTrainee(TraineeDTO traineeDTO) {
        validateTraineeDetails(traineeDTO);
        User user = userService.createUser(traineeDTO.getFirstName(), traineeDTO.getLastName());
        Trainee trainee = new Trainee();
        trainee.setUser(user);
        addDate(traineeDTO.getDateOfBirth(), trainee);
        trainee.setAddress((traineeDTO.getAddress() == null || traineeDTO.getAddress().isEmpty()) ? "" : traineeDTO.getAddress());
        trainee.setTrainers(new ArrayList<>());
        saveTrainee(trainee);
        LOGGER.info("Trainee registered: {}", trainee.getUser().getUsername());
        return trainee;
    }

    @Transactional
    public Trainee updateTrainee(TraineeDTO traineeDTO, String username, String password) {
        authenticateTrainee(username, password);
        validateTraineeDetails(traineeDTO);
        Trainee trainee = getTraineeByUsername(username);
        User user = userService.updateUser(traineeDTO.getFirstName(), traineeDTO.getLastName(), trainee.getUser());
        trainee.setUser(user);
        addDate(traineeDTO.getDateOfBirth(), trainee);
        trainee.setAddress((traineeDTO.getAddress() == null || traineeDTO.getAddress().isEmpty()) ? "" : traineeDTO.getAddress());
        saveTrainee(trainee);
        LOGGER.info("Trainee updated: {}", trainee.getUser().getUsername());
        return trainee;
    }

    @Transactional
    public void deleteTrainee(String username, String password) {
        authenticateTrainee(username, password);
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
    public User ActivateDeactivateTrainee(String username, String password, boolean isActive) {
        LOGGER.info("Activating/Deactivating trainee: {}", username);
        authenticateTrainee(username, password);
        Trainee trainee = getTraineeByUsername(username);
        User user = trainee.getUser();
        user.setIsActive(isActive);
        userService.saveUser(user);
        LOGGER.info("Trainee {} isActive: {}", username, user.getIsActive());
        return user;
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

    private void validateTraineeDetails(TraineeDTO traineeDTO) {
        LOGGER.info("Validating trainee details is not null: {}", traineeDTO);
        if (traineeDTO == null){
            LOGGER.error("Trainee details cannot be null");
            throw new MissingAttributes("Trainee details cannot be null");
        }
    }
}
