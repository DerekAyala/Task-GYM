package com.epam.taskgym.service;

import com.epam.taskgym.entity.Trainee;
import com.epam.taskgym.entity.Trainer;
import com.epam.taskgym.entity.Training;
import com.epam.taskgym.entity.TrainingType;
import com.epam.taskgym.repository.TrainingRepository;
import com.epam.taskgym.service.exception.BadRequestException;
import com.epam.taskgym.service.exception.MissingAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;

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

    public Training createTraining(Map<String, String> trainingDetails) {
        if (trainingDetails == null || trainingDetails.isEmpty()) {
            throw new MissingAttributes("Training details cannot be null or empty");
        }

        if ((!trainingDetails.containsKey("traineeUsername") || trainingDetails.get("traineeUsername").isEmpty()) &&
                (!trainingDetails.containsKey("trainerUsername") || trainingDetails.get("trainerUsername").isEmpty()) &&
                (!trainingDetails.containsKey("trainingTypeName") || trainingDetails.get("trainingTypeName").isEmpty()) &&
                (!trainingDetails.containsKey("date") || trainingDetails.get("date").isEmpty()) &&
                (!trainingDetails.containsKey("duration") || trainingDetails.get("duration").isEmpty()) &&
                (!trainingDetails.containsKey("name") || trainingDetails.get("name").isEmpty())) {
            throw new MissingAttributes("Trainee, trainer, trainingType, name, date and duration are required");
        }

        Trainee trainee = traineeService.getTraineeByUsername(trainingDetails.get("traineeUsername"));
        Trainer trainer = trainerService.getTrainerByUsername(trainingDetails.get("trainerUsername"));
        Date date = traineeService.validateDate(trainingDetails.get("date"));
        TrainingType trainingType = trainingTypeService.getTrainingTypeByName(trainingDetails.get("trainingTypeName"));
        int duration = validateDuration(trainingDetails.get("duration"));

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setName(trainingDetails.get("name"));
        training.setDate(date);
        training.setTrainingType(trainingType);
        training.setDuration(duration);

        return training;
    }

    public Integer validateDuration(String duration) {
        try {
            return Integer.parseInt(duration);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Duration must be a number");
        }
    }

    public List<Training> getTrainingsByTraineeUsername(String username) {
        return trainingRepository.findAllByTrainee_User_Username(username);
    }

    public void deleteTrainingsByTraineeUsername(String username) {
        trainingRepository.deleteAllByTrainee_User_Username(username);
    }

    public List<Training> getTrainingsByTraineeUsernameAndDateBetween(String username, Date startDate, Date endDate) {
        return trainingRepository.findAllByTrainee_User_UsernameAndDateBetween(username, startDate, endDate);
    }

    public List<Training> getTrainingsByTraineeUsernameAndTrainerName(String username, String trainerName) {
        return trainingRepository.findAllByTrainee_User_UsernameAndTrainer_User_FirstName(username, trainerName);
    }

    public List<Training> getTrainingsByTraineeUsernameAndTrainingTypeName(String username, String trainingType) {
        return trainingRepository.findAllByTrainee_User_UsernameAndTrainingType_name(username, trainingType);
    }

    public List<Training> getTrainingsByTrainerUsername(String username) {
        return trainingRepository.findAllByTrainer_User_Username(username);
    }

    public List<Training> getTrainingsByTrainerUsernameAndDateBetween(String username, Date startDate, Date endDate) {
        return trainingRepository.findAllByTrainer_User_UsernameAndDateBetween(username, startDate, endDate);
    }

    public List<Training> getTrainingsByTrainerUsernameAndTraineeName(String username, String traineeName) {
        return trainingRepository.findAllByTrainer_User_UsernameAndTrainee_User_FirstName(username, traineeName);
    }
}
