package com.epam.taskgym.service;

import com.epam.taskgym.dto.TrainingDTO;
import com.epam.taskgym.entity.Trainee;
import com.epam.taskgym.entity.Trainer;
import com.epam.taskgym.entity.Training;
import com.epam.taskgym.entity.TrainingType;
import com.epam.taskgym.repository.TrainingRepository;
import com.epam.taskgym.exception.BadRequestException;
import com.epam.taskgym.exception.MissingAttributes;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

@Service
public class TrainingService {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingRepository trainingRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingService.class);

    public TrainingService(TraineeService traineeService, TrainerService trainerService, TrainingRepository trainingRepository) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingRepository = trainingRepository;
    }

    @Transactional
    public TrainingDTO createTraining(TrainingDTO trainingDTO) {
        validateTrainingDetails(trainingDTO);
        Trainee trainee = traineeService.getTraineeByUsername(trainingDTO.getTraineeUsername());
        Trainer trainer = trainerService.getTrainerByUsername(trainingDTO.getTrainerUsername());
        return buildTraining(trainee, trainer, trainingDTO.getDate(), trainer.getSpecialization(), trainingDTO.getDuration(), trainingDTO.getName());
    }

    public List<Training> getTrainingsByTraineeUsername(String username) {
        LOGGER.info("Finding trainings by trainee username: {}", username);
        if (username == null || username.isEmpty()) {
            LOGGER.error("Username is required");
            throw new MissingAttributes("Username is required");
        }
        return trainingRepository.findAllByTrainee_User_Username(username);
    }

    public List<Training> getTrainingsByTraineeUsernameAndDateBetween(String username, String startDate, String endDate) {
        LOGGER.info("Finding trainings by trainee username: {} and date between: {} and {}", username, startDate, endDate);
        if (username == null || username.isEmpty() || startDate == null || endDate == null) {
            LOGGER.error("Username is required");
            throw new MissingAttributes("Username is required");
        }
        return trainingRepository.findAllByTrainee_User_UsernameAndDateBetween(username, traineeService.validateDate(startDate), traineeService.validateDate(endDate));
    }

    public List<Training> getTrainingsByTraineeUsernameAndTrainerName(String username, String trainerName) {
        LOGGER.info("Finding trainings by trainee username: {} and trainer name: {}", username, trainerName);
        if (username == null || username.isEmpty() || trainerName == null || trainerName.isEmpty()) {
            LOGGER.error("Username and trainer name are required");
            throw new MissingAttributes("Username and trainer name are required");
        }
        return trainingRepository.findAllByTrainee_User_UsernameAndTrainer_User_FirstName(username, trainerName);
    }

    public List<Training> getTrainingsByTraineeUsernameAndTrainingTypeName(String username, String trainingType) {
        LOGGER.info("Finding trainings by trainee username: {} and training type: {}", username, trainingType);
        if (username == null || username.isEmpty() || trainingType == null || trainingType.isEmpty()) {
            LOGGER.error("Username and training type are required");
            throw new MissingAttributes("Username and training type are required");
        }
        return trainingRepository.findAllByTrainee_User_UsernameAndTrainingType_name(username, trainingType);
    }

    public List<Training> getTrainingsByTrainerUsername(String username) {
        LOGGER.info("Finding trainings by trainer username: {}", username);
        if (username == null || username.isEmpty()) {
            LOGGER.error("Username is required");
            throw new MissingAttributes("Username is required");
        }
        return trainingRepository.findAllByTrainer_User_Username(username);
    }

    public List<Training> getTrainingsByTrainerUsernameAndDateBetween(String username, String startDate, String endDate) {
        LOGGER.info("Finding trainings by trainer username: {} and date between: {} and {}", username, startDate, endDate);
        if (username == null || username.isEmpty() || startDate == null || endDate == null) {
            LOGGER.error("Username is required");
            throw new MissingAttributes("Username is required");
        }
        return trainingRepository.findAllByTrainer_User_UsernameAndDateBetween(username, traineeService.validateDate(startDate), traineeService.validateDate(endDate));
    }

    public List<Training> getTrainingsByTrainerUsernameAndTraineeName(String username, String traineeName) {
        LOGGER.info("Finding trainings by trainer username: {} and trainee name: {}", username, traineeName);
        if (username == null || username.isEmpty() || traineeName == null || traineeName.isEmpty()) {
            LOGGER.error("Username and trainee name are required");
            throw new MissingAttributes("Username and trainee name are required");
        }
        return trainingRepository.findAllByTrainer_User_UsernameAndTrainee_User_FirstName(username, traineeName);
    }

    public Integer validateDuration(String duration) {
        LOGGER.info("Validating duration: {}", duration);
        try {
            return Integer.parseInt(duration);
        } catch (NumberFormatException e) {
            LOGGER.error("Duration must be a number: " + e);
            throw new BadRequestException("Duration must be a number");
        }
    }

    private TrainingDTO buildTraining(Trainee trainee, Trainer trainer, Date date, TrainingType trainingType, int duration, String name) {
        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setName(name);
        training.setDate(date);
        training.setTrainingType(trainingType);
        training.setDuration(duration);
        trainingRepository.save(training);
        LOGGER.info("Successfully created training: {}", training);
        return new TrainingDTO(trainee.getUser().getFirstName(), trainer.getUser().getFirstName(), date, duration, name);
    }

    private void validateTrainingDetails(TrainingDTO trainingDTO) {
        LOGGER.info("Validating training details: {}", trainingDTO);
        if (trainingDTO == null) {
            LOGGER.error("Training details are required");
            throw new MissingAttributes("Training details are required");
        }
        if (trainingDTO.getTraineeUsername() == null || trainingDTO.getTraineeUsername().isEmpty() ||
                trainingDTO.getTrainerUsername() == null || trainingDTO.getTrainerUsername().isEmpty() ||
                trainingDTO.getDate() == null ||
                trainingDTO.getName() == null || trainingDTO.getName().isEmpty() ||
                trainingDTO.getDuration() <= 0) {
            LOGGER.error("Trainee username, trainer username, date, training type name, name and duration are required");
            throw new MissingAttributes("Trainee username, trainer username, date, training type name, name and duration are required");
        }
    }
}
