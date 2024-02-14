package com.epam.taskgym.service;

import com.epam.taskgym.entity.Trainee;
import com.epam.taskgym.entity.Trainer;
import com.epam.taskgym.entity.Training;
import com.epam.taskgym.entity.TrainingType;
import com.epam.taskgym.repository.TrainingRepository;
import com.epam.taskgym.service.exception.BadRequestException;
import com.epam.taskgym.service.exception.MissingAttributes;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TrainingService {

    @Autowired
    private TraineeService traineeService;
    @Autowired
    private TrainerService trainerService;
    @Autowired
    private TrainingTypeService trainingTypeService;
    @Autowired
    private TrainingRepository trainingRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingService.class);

    @Transactional
    public Training createTraining(Map<String, String> trainingDetails) {
        validateTrainingDetails(trainingDetails);
        Trainee trainee = traineeService.getTraineeByUsername(trainingDetails.get("traineeUsername"));
        Trainer trainer = trainerService.getTrainerByUsername(trainingDetails.get("trainerUsername"));
        Date date = traineeService.validateDate(trainingDetails.get("date"));
        TrainingType trainingType = trainingTypeService.getTrainingTypeByName(trainingDetails.get("trainingTypeName"));
        int duration = validateDuration(trainingDetails.get("duration"));

        return buildTraining(trainee, trainer, date, trainingType, duration, trainingDetails.get("name"));
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

    private Integer validateDuration(String duration) {
        LOGGER.info("Validating duration: {}", duration);
        try {
            return Integer.parseInt(duration);
        } catch (NumberFormatException e) {
            LOGGER.error("Duration must be a number: " + e);
            throw new BadRequestException("Duration must be a number");
        }
    }

    private Training buildTraining(Trainee trainee, Trainer trainer, Date date, TrainingType trainingType, int duration, String name) {
        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setName(name);
        training.setDate(date);
        training.setTrainingType(trainingType);
        training.setDuration(duration);
        trainingRepository.save(training);
        LOGGER.info("Successfully created training: {}", training);
        return training;
    }

    private void validateTrainingDetails(Map<String, String> trainingDetails) {
        LOGGER.info("Validating training details: {}", trainingDetails);
        List<String> missingFields = Stream
                .of("traineeUsername", "trainerUsername", "trainingTypeName", "date", "duration", "name")
                .filter(field -> !trainingDetails.containsKey(field) || trainingDetails.get(field).isEmpty())
                .collect(Collectors.toList());

        if (!missingFields.isEmpty()) {
            LOGGER.error("Fields missing or empty: {}", missingFields);
            throw new MissingAttributes("Fields missing or empty: " + String.join(", ", missingFields));
        }
    }
}
