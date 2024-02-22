package com.epam.taskgym;

import com.epam.taskgym.entity.Trainee;
import com.epam.taskgym.entity.Trainer;
import com.epam.taskgym.entity.Training;
import com.epam.taskgym.entity.TrainingType;
import com.epam.taskgym.repository.TrainingRepository;
import com.epam.taskgym.service.TraineeService;
import com.epam.taskgym.service.TrainerService;
import com.epam.taskgym.service.TrainingService;
import com.epam.taskgym.service.TrainingTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

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
    private TrainingTypeService trainingTypeService;

    Trainee trainee;
    Trainer trainer;
    TrainingType trainingType;
    Date date;
    Training training;

    @BeforeEach
    public void setup() {
        trainee = new Trainee();
        trainer = new Trainer();
        trainingType = new TrainingType();
        date = new Date();
        training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);
        training.setDate(date);
        training.setDuration(1);
    }
}
