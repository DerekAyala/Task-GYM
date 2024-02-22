package com.epam.taskgym.service;

import com.epam.taskgym.dto.TrainingDTO;
import com.epam.taskgym.dto.TrainingFilteredDTO;
import com.epam.taskgym.dto.TrainingResponse;
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

import java.util.ArrayList;
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
        manyToManyTrainerAndTrainee(trainee, trainer);
        return buildTraining(trainee, trainer, trainingDTO.getDate(), trainer.getSpecialization(), trainingDTO.getDuration(), trainingDTO.getName());
    }

    @Transactional
    public void manyToManyTrainerAndTrainee(Trainee trainee, Trainer trainer){
        LOGGER.info("Creating many to many relationship between trainee: {} and trainer: {}", trainee.getUser().getUsername(), trainer.getUser().getUsername());
        List<Trainer> trainers = trainee.getTrainers();
        List<Trainee> trainees = trainer.getTrainees();
        if (!trainers.contains(trainer)) {
            trainers.add(trainer);
            trainerService.saveTrainer(trainer);
        }
        if (!trainees.contains(trainee)) {
            trainees.add(trainee);
            traineeService.saveTrainee(trainee);
        }
    }

    public List<TrainingResponse> getTraineeTrainingsFiltered(TrainingFilteredDTO trainingFilteredDTO) {
        LOGGER.info("Getting trainee trainings filtered by: {}", trainingFilteredDTO);
        validateUsername(trainingFilteredDTO.getUsername());
        List<Training> trainings = trainingRepository.getTraineeFilteredTrainings(trainingFilteredDTO.getUsername(), trainingFilteredDTO.getDateFrom(), trainingFilteredDTO.getDateTo(), trainingFilteredDTO.getTrainingTypeName(), trainingFilteredDTO.getTrainerOrTraineeName());
        return convertTrainingsToTrainingResponse(trainings, true);

    }

    public List<TrainingResponse> getTrainerTrainingsFiltered(TrainingFilteredDTO trainingFilteredDTO) {
        LOGGER.info("Getting trainer trainings filtered by: {}", trainingFilteredDTO);
        validateUsername(trainingFilteredDTO.getUsername());
        List<Training> trainings = trainingRepository.getTrainerFilteredTrainings(trainingFilteredDTO.getUsername(), trainingFilteredDTO.getDateFrom(), trainingFilteredDTO.getDateTo(), trainingFilteredDTO.getTrainerOrTraineeName());
        return convertTrainingsToTrainingResponse(trainings, false);
    }

    public List<TrainingResponse> convertTrainingsToTrainingResponse(List<Training> trainings, boolean isTrainee) {
        LOGGER.info("Converting trainings to training response");
        List<TrainingResponse> trainingResponses = new ArrayList<>();
        for (Training training : trainings) {
            TrainingResponse trainingResponse = new TrainingResponse();
            trainingResponse.setTrainingName(training.getName());
            trainingResponse.setTrainingDate(training.getDate());
            trainingResponse.setDuration(training.getDuration());
            trainingResponse.setTrainingType(training.getTrainingType());
            trainingResponse.setTraineeOrTrainerName(isTrainee ? training.getTrainer().getUser().getFirstName() : training.getTrainee().getUser().getFirstName());
            trainingResponses.add(trainingResponse);
        }
        return trainingResponses;
    }

    public void validateUsername(String username) {
        LOGGER.info("Validating username: {}", username);
        if (username == null || username.isEmpty()) {
            LOGGER.error("Username is required");
            throw new MissingAttributes("Username is required");
        }
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
        LOGGER.info("Successfully created training: {}", training.getName());
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
