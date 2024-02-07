package com.epam.taskgym;

import com.epam.taskgym.dao.*;
import com.epam.taskgym.dto.TraineeDTO;
import com.epam.taskgym.dto.TrainerDTO;
import com.epam.taskgym.entity.Training;
import com.epam.taskgym.entity.TrainingType;
import com.epam.taskgym.service.TraineeService;
import com.epam.taskgym.service.TrainerService;
import com.epam.taskgym.service.TrainingService;
import com.epam.taskgym.storage.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TrainingServiceTest {

    private UserDAO userDAO;
    private TrainerDAO trainerDAO;
    private TraineeDAO traineeDAO;
    private TrainingDAO trainingDAO;
    private TrainingTypeDAO trainingTypeDAO;
    private TrainingType trainingType;
    private TrainingService trainingService;
    private TrainerService trainerService;
    private TraineeService traineeService;
    private TrainerDTO trainerDTOTest;
    private TraineeDTO traineeDTOTest;
    private Training trainingTest;

    @BeforeEach
    public void setup(){
        UserInMemoryDb userInMemoryDb = new UserInMemoryDb();
        userDAO = new UserDAO(userInMemoryDb);
        trainingTypeDAO = new TrainingTypeDAO(new TrainingTypeInMemoryDb());
        trainerDAO = new TrainerDAO(new TrainerInMemoryDb(), userInMemoryDb);
        traineeDAO = new TraineeDAO(new TraineeInMemoryDb(), userInMemoryDb);
        trainingDAO = new TrainingDAO(new TrainingInMemoryDb());
        traineeService = new TraineeService(userDAO, traineeDAO);
        trainerService = new TrainerService(userDAO, trainerDAO, traineeDAO);
        trainingService = new TrainingService(trainingDAO, trainerDAO, traineeDAO, trainingTypeDAO);
        traineeDTOTest = traineeService.registerTrainee("First", "Last", "2000-07-10", "123 Trainee Street");
        trainerDTOTest = trainerService.registerTrainer("First", "Last", "Cardio");
        trainingType = new TrainingType();
        trainingType.setName("Cardio");
        trainingType = trainingTypeDAO.save(trainingType);
    }

    @Test
    public void testCreateTraining() {
        Training training = trainingService.createTraining(
                traineeDTOTest.getTraineeId(),
                trainerDTOTest.getTrainerId(),
                "Sample Training",
                trainingType.getId());

        assertNotNull(training, "Training should not be null");
        assertNotNull(training.getId(), "Training ID should not be null");
        assertEquals(traineeDTOTest.getTraineeId(), training.getTraineeId(), "TraineeId should match");
        assertEquals(trainerDTOTest.getTrainerId(), training.getTrainerId(), "TrainerId should match");
        assertEquals("Sample Training", training.getName(), "Training name should match");
        assertEquals(trainingType.getId(), training.getTrainingTypeId(), "TrainingTypeId should match");
    }

    @Test
    public void testGetTraining() {
        trainingTest = trainingService.createTraining(traineeDTOTest.getTraineeId(), trainerDTOTest.getTrainerId(), "Cardio", trainingType.getId());
        Training fetchedTraining = trainingService.getTraining(trainingTest.getId());

        assertNotNull(fetchedTraining, "Fetched Training should not be null");
        assertEquals(trainingTest.getId(), fetchedTraining.getId(), "IDs of fetched and original Training should match");
    }
}
