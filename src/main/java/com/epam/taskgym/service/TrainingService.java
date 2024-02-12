package com.epam.taskgym.service;

import com.epam.taskgym.entity.Trainee;
import com.epam.taskgym.entity.Trainer;
import com.epam.taskgym.entity.Training;
import com.epam.taskgym.entity.TrainingType;
import com.epam.taskgym.service.exception.MissingAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
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
    private UserService userService;

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
            (!trainingDetails.containsKey("name") || trainingDetails.get("name").isEmpty())){
            throw new MissingAttributes("Trainee, trainer, trainingType, name, date and duration are required");
        }
        Trainee trainee = traineeService.getTraineeByUsername(trainingDetails.get("traineeUsername"));
        Trainer trainer = trainerService.getTrainerByUsername(trainingDetails.get("trainerUsername"));
        TrainingType trainingType = trainingTypeService.getTrainingTypeByName(trainingDetails.get("trainingTypeName"));
        Date date = userService.validateDate(trainingDetails.get("date"));
        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);
        training.setName(trainingDetails.get("name"));

        return new Training();
    }

    public Training getTraining(Long id) {
        return new Training();
    }
}
