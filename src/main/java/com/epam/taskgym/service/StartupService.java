package com.epam.taskgym.service;

import com.epam.taskgym.models.TraineeDTO;
import com.epam.taskgym.models.TrainerDTO;
import com.epam.taskgym.models.TrainingDTO;
import com.epam.taskgym.helpers.Validations;
import com.epam.taskgym.repository.TraineeRepository;
import com.epam.taskgym.repository.TrainerRepository;
import com.epam.taskgym.repository.TrainingRepository;
import com.epam.taskgym.repository.TrainingTypeRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class StartupService {

    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainingTypeService trainingTypeService;
    private final TraineeService traineeService;
    private final TraineeRepository traineeRepository;
    private final TrainerService trainerService;
    private final TrainerRepository trainerRepository;
    private final TrainingService trainingService;
    private final TrainingRepository trainingRepository;

    public StartupService(TrainingTypeRepository trainingTypeRepository, TrainingTypeService trainingTypeService, TraineeService traineeService, TraineeRepository traineeRepository, TrainerService trainerService, TrainerRepository trainerRepository, TrainingService trainingService, TrainingRepository trainingRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
        this.trainingTypeService = trainingTypeService;
        this.traineeService = traineeService;
        this.traineeRepository = traineeRepository;
        this.trainerService = trainerService;
        this.trainerRepository = trainerRepository;
        this.trainingService = trainingService;
        this.trainingRepository = trainingRepository;
    }

    @PostConstruct
    public void init() {
        insertDefaultTrainingTypes();
        insertDefaultTrainees();
        insertDefaultTrainers();
        insertDefaultTraining();
    }

    private void insertDefaultTrainingTypes() {
        List<String> defaultTypes = Arrays.asList("Functional Training", "Mobility Training", "Strength Training", "Balance Training", "Agility Training", "Stretching");

        defaultTypes.forEach(type -> {
            if (!trainingTypeRepository.existsByName(type)) {
                trainingTypeService.RegisterTrainingType(type);
            }
        });
    }

    private void insertDefaultTrainees() {
        List<Map<String, String>> defaultTrainees = Arrays.asList(
                Map.of("username", "derek.ayala", "firstName", "Derek", "lastName", "Ayala", "dateOfBirth", "20-01-2001", "address", "1234 Main St"),
                Map.of("username", "sarah.jones", "firstName", "Sarah", "lastName", "Jones", "dateOfBirth", "02-05-1995", "address", "4567 Oak St"),
                Map.of("username", "michael.smith", "firstName", "Michael", "lastName", "Smith", "dateOfBirth", "14-06-1987", "address", "7890 Pine St"),
                Map.of("username", "lisa.johnson", "firstName", "Lisa", "lastName", "Johnson", "dateOfBirth", "29-03-1999", "address", "321 Birch St"),
                Map.of("username", "peter.brown", "firstName", "Peter", "lastName", "Brown", "dateOfBirth", "23-09-2000", "address", "654 Elm St"),
                Map.of("username", "mary.white", "firstName", "Mary", "lastName", "White", "dateOfBirth", "21-11-1997", "address", "987 Walnut St"),
                Map.of("username", "john.doe", "firstName", "John", "lastName", "Doe", "dateOfBirth", "18-08-2002", "address", "1600 Chestnut St"),
                Map.of("username", "jane.doe", "firstName", "Jane", "lastName", "Doe", "dateOfBirth", "12-02-1996", "address", "4321 Ash St"),
                Map.of("username", "carl.miller", "firstName", "Carl", "lastName", "Miller", "dateOfBirth", "22-07-2003", "address", "5123 Cypress St"),
                Map.of("username", "rita.harris", "firstName", "Rita", "lastName", "Harris", "dateOfBirth", "01-04-1988", "address", "6789 Beech St")
        );
        defaultTrainees.forEach(trainee -> {
            if (traineeRepository.findByUserUsername(trainee.get("username")).isEmpty()) {
                TraineeDTO traineeDTO = new TraineeDTO();
                traineeDTO.setFirstName(trainee.get("firstName"));
                traineeDTO.setLastName(trainee.get("lastName"));
                traineeDTO.setDateOfBirth(Validations.validateDate(trainee.get("dateOfBirth")));
                traineeDTO.setAddress(trainee.get("address"));
                traineeService.registerTrainee(traineeDTO);
            }
        });
    }

    private void insertDefaultTrainers() {
        List<Map<String, String>> defaultTrainers = Arrays.asList(
                Map.of("username", "alexis.vega", "firstName", "Alexis", "lastName", "Vega","specialization", "Functional Training"),
                Map.of("username", "beatrice.stone", "firstName", "Beatrice", "lastName", "Stone", "specialization", "Mobility Training"),
                Map.of("username", "charles.jennings", "firstName", "Charles", "lastName", "Jennings", "specialization", "Strength Training"),
                Map.of("username", "diana.freeman", "firstName", "Diana", "lastName", "Freeman", "specialization", "Balance Training"),
                Map.of("username", "elvis.mitchell", "firstName", "Elvis", "lastName", "Mitchell", "specialization", "Agility Training")
        );
        defaultTrainers.forEach(trainer -> {
            if (trainerRepository.findByUserUsername(trainer.get("username")).isEmpty()) {
                TrainerDTO trainerDTO = new TrainerDTO();
                trainerDTO.setFirstName(trainer.get("firstName"));
                trainerDTO.setLastName(trainer.get("lastName"));
                trainerDTO.setSpecialization(trainer.get("specialization"));
                trainerService.registerTrainer(trainerDTO);
            }
        });
    }

    private void insertDefaultTraining() {
        List<Map<String, String>> defaultTrainings = Arrays.asList(
                Map.of("traineeUsername", "derek.ayala", "trainerUsername", "beatrice.stone", "date", "14-02-2024", "duration", "60", "name", "Mobility Training 1"),
                Map.of("traineeUsername", "derek.ayala", "trainerUsername", "charles.jennings", "date", "20-02-2024", "duration", "60", "name", "Strength Training 1"),
                Map.of("traineeUsername", "sarah.jones", "trainerUsername", "diana.freeman", "date", "01-03-2024", "duration", "60", "name", "Strength Training 2"),
                Map.of("traineeUsername", "sarah.jones", "trainerUsername", "elvis.mitchell", "date", "10-03-2024", "duration", "60", "name", "Agility Training 1"),
                Map.of("traineeUsername", "michael.smith", "trainerUsername", "beatrice.stone", "date", "15-04-2024", "duration", "60", "name", "Functional Training 1"),
                Map.of("traineeUsername", "michael.smith", "trainerUsername", "charles.jennings", "date", "28-04-2024", "duration", "60", "name", "Mobility Training 2"),
                Map.of("traineeUsername", "lisa.johnson", "trainerUsername", "diana.freeman", "date", "19-05-2024", "duration", "60", "name", "Balance Training 1"),
                Map.of("traineeUsername", "lisa.johnson", "trainerUsername", "elvis.mitchell", "date", "26-05-2024", "duration", "60", "name", "Functional Training 2"),
                Map.of("traineeUsername", "peter.brown", "trainerUsername", "beatrice.stone", "date", "14-06-2024", "duration", "60", "name", "Agility Training 2"),
                Map.of("traineeUsername", "peter.brown", "trainerUsername", "charles.jennings", "date", "21-06-2024", "duration", "60", "name", "Balance Training 2")
                // Additional trainings...
        );

        defaultTrainings.forEach(training -> {
            if (trainingRepository.findByTrainee_User_UsernameAndTrainer_User_UsernameAndDate(training.get("traineeUsername"), training.get("trainerUsername"), Validations.validateDate(training.get("date"))).isEmpty()) {
                TrainingDTO trainingDTO = new TrainingDTO();
                trainingDTO.setTraineeUsername(training.get("traineeUsername"));
                trainingDTO.setTrainerUsername(training.get("trainerUsername"));
                trainingDTO.setDate(Validations.validateDate(training.get("date")));
                trainingDTO.setDuration(Validations.validateDuration(training.get("duration")));
                trainingDTO.setName(training.get("name"));
                trainingService.createTraining(trainingDTO);
            }
        });
    }
}

