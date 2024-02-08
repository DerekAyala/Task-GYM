package com.epam.taskgym.service;

import com.epam.taskgym.entity.Training;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TrainingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingService.class);

    public Training createTraining(Long traineeId, Long trainerId, String name, Long trainingTypeId) {
        /*
        Optional<Trainee> traineeOptional = traineeDAO.findById(traineeId);
        Optional<Trainer> trainerOptional = trainerDAO.findById(trainerId);
        Optional<TrainingType> trainingTypeOptional = trainingTypeDAO.findById(trainingTypeId);

        if (traineeOptional.isEmpty() || trainerOptional.isEmpty() || trainingTypeOptional.isEmpty()) {
            throw new RuntimeException("Either Trainee, Trainer, or Training Type does not exist");
        }

        Training training = new Training();
        training.setTraineeId(traineeId);
        training.setTrainerId(trainerId);
        training.setName(name);
        training.setTrainingTypeId(trainingTypeId);
        training = trainingDAO.save(training);
        LOGGER.info("Training saved with ID: {}", training.getId());
        */

        return new Training();
    }

    public Training getTraining(Long id) {
        /*
        Optional<Training> training = trainingDAO.findById(id);
        return training.orElse(null);
        */
        return new Training();
    }
}
