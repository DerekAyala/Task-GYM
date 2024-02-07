package com.epam.taskgym.service;

import com.epam.taskgym.dao.TraineeDAO;
import com.epam.taskgym.dao.TrainerDAO;
import com.epam.taskgym.dao.TrainingDAO;
import com.epam.taskgym.dao.TrainingTypeDAO;
import com.epam.taskgym.entity.Trainee;
import com.epam.taskgym.entity.Trainer;
import com.epam.taskgym.entity.Training;
import com.epam.taskgym.entity.TrainingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class TrainingService {

    private final TrainingDAO trainingDAO;
    private final TrainerDAO trainerDAO;
    private final TraineeDAO traineeDAO;
    private final TrainingTypeDAO trainingTypeDAO;

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingService.class);

    @Autowired
    public TrainingService(TrainingDAO trainingDAO, TrainerDAO trainerDAO, TraineeDAO traineeDAO, TrainingTypeDAO trainingTypeDAO) {
        this.trainingDAO = trainingDAO;
        this.trainerDAO = trainerDAO;
        this.traineeDAO = traineeDAO;
        this.trainingTypeDAO = trainingTypeDAO;
    }

    public Training createTraining(Long traineeId, Long trainerId, String name, Long trainingTypeId) {
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
        LOGGER.info("User saved with ID: {}", training.getId());
        return training;
    }

    public Training getTraining(Long id) {
        Optional<Training> training = trainingDAO.findById(id);
        return training.orElse(null);
    }
}
