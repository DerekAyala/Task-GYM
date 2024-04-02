package com.epam.taskgym.helpers;

import com.epam.taskgym.models.*;
import com.epam.taskgym.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Builders {
    private static final Logger LOGGER = LoggerFactory.getLogger(Builders.class);
    public static User buildUser(String firstName, String lastName, String username, String password, String role) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(password);
        user.setIsActive(true);
        user.setRole(role);
        LOGGER.info("User successfully built: {}", username);
        return user;
    }

    public static Training buildTraining(Trainee trainee, Trainer trainer, Date date, TrainingType trainingType, int duration, String name) {
        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setName(name);
        training.setDate(date);
        training.setTrainingType(trainingType);
        training.setDuration(duration);
        return training;
    }

    public static String generateRandomPassword() {
        LOGGER.info("Generating random password");
        String password = new Random().ints(48, 122)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .mapToObj(i -> (char) i)
                .limit(10)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
        LOGGER.info("Random password generated successfully");
        return password;
    }

    public static TraineeDTO convertTraineeToTraineeDTO(Trainee trainee) {
        TraineeDTO traineeDTO = new TraineeDTO();
        traineeDTO.setFirstName(trainee.getUser().getFirstName());
        traineeDTO.setLastName(trainee.getUser().getLastName());
        traineeDTO.setDateOfBirth(trainee.getDateOfBirth());
        traineeDTO.setAddress(trainee.getAddress());
        traineeDTO.setIsActive(trainee.getUser().getIsActive());
        traineeDTO.setTrainers(convertTrainersToTrainerListItem(trainee.getTrainers()));
        return traineeDTO;
    }

    public static ArrayList<TrainerListItem> convertTrainersToTrainerListItem(List<Trainer> trainers) {
        ArrayList<TrainerListItem> trainerListItem = new ArrayList<>();
        trainers.forEach(trainer -> {
            TrainerListItem trainerDTO = new TrainerListItem();
            trainerDTO.setFirstName(trainer.getUser().getFirstName());
            trainerDTO.setLastName(trainer.getUser().getLastName());
            trainerDTO.setUsername(trainer.getUser().getUsername());
            trainerDTO.setSpecialization(trainer.getSpecialization().getName());
            trainerListItem.add(trainerDTO);
        });
        return trainerListItem;
    }

    public static TrainerDTO convertTrainerToTraineeDTO(Trainer trainer) {
        TrainerDTO trainerDTO = new TrainerDTO();
        trainerDTO.setFirstName(trainer.getUser().getFirstName());
        trainerDTO.setLastName(trainer.getUser().getLastName());
        trainerDTO.setSpecialization(trainer.getSpecialization().getName());
        trainerDTO.setActive(trainer.getUser().getIsActive());
        trainerDTO.setTrainees(convertTrainerListToTraineeListItem(trainer.getTrainees()));
        return trainerDTO;
    }

    public static List<TraineeListItem> convertTrainerListToTraineeListItem(List<Trainee> trainees) {
        List<TraineeListItem> traineeListItems = new ArrayList<>();
        for (Trainee trainee : trainees) {
            TraineeListItem traineeListItem = new TraineeListItem();
            traineeListItem.setFirstName(trainee.getUser().getFirstName());
            traineeListItem.setLastName(trainee.getUser().getLastName());
            traineeListItem.setUsername(trainee.getUser().getUsername());
            traineeListItems.add(traineeListItem);
        }
        return traineeListItems;
    }

    public static List<TrainingResponse> convertTrainingsToTrainingResponse(List<Training> trainings, boolean isTrainee) {
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
}
