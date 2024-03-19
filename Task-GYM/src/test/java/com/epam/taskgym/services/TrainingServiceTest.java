package com.epam.taskgym.services;

import com.epam.taskgym.client.MicroserviceClient;
import com.epam.taskgym.models.TrainingDTO;
import com.epam.taskgym.models.TrainingFilteredDTO;
import com.epam.taskgym.models.TrainingResponse;
import com.epam.taskgym.entity.*;
import com.epam.taskgym.repository.TrainingRepository;
import com.epam.taskgym.service.TraineeService;
import com.epam.taskgym.service.TrainerService;
import com.epam.taskgym.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @InjectMocks
    private TrainingService trainingService;

    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainerService trainerService;
    @Mock
    private MicroserviceClient microserviceClient;

    Trainee trainee;
    Trainer trainer;
    TrainingType trainingType;
    Date date;
    Training training;

    @BeforeEach
    public void setup() {
        trainee = new Trainee();
        trainer = new Trainer();
        trainee.setUser(new User());
        trainer.setUser(new User());
        trainee.setTrainers(new ArrayList<>());
        trainer.setTrainees(new ArrayList<>());
        trainer.setSpecialization(new TrainingType());
        trainingType = new TrainingType();
        date = new Date();
        training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);
        training.setDate(date);
        training.setDuration(1);
    }

    @Test
    void testAddTraining(){
        TrainingDTO trainingDTO = new TrainingDTO();
        trainingDTO.setDate(date);
        trainingDTO.setDuration(1);
        trainingDTO.setTraineeUsername("trainee");
        trainingDTO.setTrainerUsername("trainer");
        trainingDTO.setName("name");

        when(traineeService.getTraineeByUsername(any())).thenReturn(trainee);
        when(trainerService.getTrainerByUsername(any())).thenReturn(trainer);
        when(trainingRepository.save(any(Training.class))).thenReturn(training);

        TrainingDTO result = trainingService.createTraining(trainingDTO);

        assertEquals(training.getTrainee(), trainee);
        assertEquals(training.getTrainer(), trainer);
        verify(traineeService, times(1)).getTraineeByUsername(any());
        verify(trainerService, times(1)).getTrainerByUsername(any());
        verify(trainingRepository, times(1)).save(any(Training.class));
    }

    @Test
    void testGetTrainingsByTrainee() {
        String username = "username";
        TrainingFilteredDTO trainingFilteredDTO = new TrainingFilteredDTO();
        trainingFilteredDTO.setUsername(username);

        when(trainingRepository.getTraineeFilteredTrainings(username, null,null,null,null)).thenReturn(new ArrayList<>());

        List<TrainingResponse> result = trainingService.getTraineeTrainingsFiltered(trainingFilteredDTO);

        assertEquals(0, result.size());
    }
}
