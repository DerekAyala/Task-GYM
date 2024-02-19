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
import com.epam.taskgym.exception.BadRequestException;
import com.epam.taskgym.exception.MissingAttributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

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

    @BeforeEach
    public void setup() {
        trainee = new Trainee();
        trainer = new Trainer();
        trainingType = new TrainingType();
        date = new Date(); //assuming the format of date is correct here
    }

    @Test
    public void createTraining_whenValidInput_shouldReturnTraining() {
        Map<String, String> trainingDetails = new HashMap<>();
        trainingDetails.put("traineeUsername", "aUser");
        trainingDetails.put("trainerUsername", "aTrainer");
        trainingDetails.put("trainingTypeName", "aType");
        trainingDetails.put("date", "29-09-2021");
        trainingDetails.put("duration", "1");
        trainingDetails.put("name", "aName");

        when(traineeService.getTraineeByUsername(anyString())).thenReturn(trainee);
        when(trainerService.getTrainerByUsername(anyString())).thenReturn(trainer);
        when(trainingTypeService.getTrainingTypeByName(anyString())).thenReturn(trainingType);
        when(traineeService.validateDate(anyString())).thenReturn(date);

        Training training = trainingService.createTraining(trainingDetails);
        assertEquals(trainee, training.getTrainee());
        assertEquals(trainer, training.getTrainer());
        assertEquals(trainingType, training.getTrainingType());
        assertEquals(1, training.getDuration());
        assertEquals(date, training.getDate());
    }

    @Test
    public void createTraining_whenInvalidDuration_shouldThrowException() {
        Map<String, String> trainingDetails = new HashMap<>();
        trainingDetails.put("traineeUsername", "aUser");
        trainingDetails.put("trainerUsername", "aTrainer");
        trainingDetails.put("trainingTypeName", "aType");
        trainingDetails.put("date", "29-09-2021");
        trainingDetails.put("duration", "NotANumber");
        trainingDetails.put("name", "aName");

        assertThrows(BadRequestException.class, () ->
                trainingService.createTraining(trainingDetails));
    }

    @Test
    void getTrainingsByTraineeUsername_shouldReturnTrainings() {
        when(trainingRepository.findAllByTrainee_User_Username(anyString()))
                .thenReturn(Arrays.asList(new Training(), new Training()));

        List<Training> result = trainingService.getTrainingsByTraineeUsername("john.doe");

        assertEquals(2, result.size());
    }

    @Test
    void getTrainingsByTraineeUsernameAndDateBetween_shouldReturnTrainings() {
        String startDate = "20-09-2021";
        String endDate = "29-09-2021";

        when(traineeService.validateDate("20-09-2021")).thenReturn(new Date());
        when(traineeService.validateDate("29-09-2021")).thenReturn(new Date());

        when(trainingRepository.findAllByTrainee_User_UsernameAndDateBetween(anyString(), any(Date.class), any(Date.class)))
                .thenReturn(Arrays.asList(new Training(), new Training()));

        List<Training> result = trainingService.getTrainingsByTraineeUsernameAndDateBetween("john.doe", startDate, endDate);

        assertEquals(2, result.size());
    }

    @Test
    void getTrainingsByTraineeUsernameAndTrainerName_shouldReturnTrainings() {
        when(trainingRepository.findAllByTrainee_User_UsernameAndTrainer_User_FirstName(anyString(), anyString()))
                .thenReturn(Arrays.asList(new Training(), new Training()));

        List<Training> result = trainingService.getTrainingsByTraineeUsernameAndTrainerName("john.doe", "trainer");

        assertEquals(2, result.size());
    }

    @Test
    void getTrainingsByTraineeUsernameAndTrainingTypeName_shouldReturnTrainings() {
        when(trainingRepository.findAllByTrainee_User_UsernameAndTrainingType_name(anyString(), anyString()))
                .thenReturn(Arrays.asList(new Training()));

        List<Training> result = trainingService.getTrainingsByTraineeUsernameAndTrainingTypeName("john.doe", "type");

        assertEquals(1, result.size());
    }

    @Test
    void getTrainingsByTrainerUsername_shouldReturnTrainings() {
        when(trainingRepository.findAllByTrainer_User_Username(anyString()))
                .thenReturn(Arrays.asList(new Training(), new Training()));

        List<Training> result = trainingService.getTrainingsByTrainerUsername("trainer.doe");

        assertEquals(2, result.size());
    }

    @Test
    void getTrainingsByTrainerUsernameAndDateBetween_shouldReturnTrainings() {
        String startDate = "20-09-2021";
        String endDate = "29-09-2021";

        when(traineeService.validateDate("20-09-2021")).thenReturn(new Date());
        when(traineeService.validateDate("29-09-2021")).thenReturn(new Date());

        when(trainingRepository.findAllByTrainer_User_UsernameAndDateBetween(anyString(), any(Date.class), any(Date.class)))
                .thenReturn(Arrays.asList(new Training()));

        List<Training> result = trainingService.getTrainingsByTrainerUsernameAndDateBetween("trainer.doe", startDate, endDate);

        assertEquals(1, result.size());
    }

    @Test
    void getTrainingsByTrainerUsernameAndTraineeName_shouldReturnTrainings() {
        when(trainingRepository.findAllByTrainer_User_UsernameAndTrainee_User_FirstName(anyString(), anyString()))
                .thenReturn(Arrays.asList(new Training(), new Training()));

        List<Training> result = trainingService.getTrainingsByTrainerUsernameAndTraineeName("trainer.doe", "john");

        assertEquals(2, result.size());
    }

    @Test
    public void getTrainingsByTraineeUsername_shouldThrowMissingAttributes_whenUsernameIsEmpty() {
        assertThrows(MissingAttributes.class, () -> trainingService.getTrainingsByTraineeUsername(""));
    }

    @Test
    public void getTrainingsByTraineeUsername_shouldThrowMissingAttributes_whenUsernameIsNull() {
        assertThrows(MissingAttributes.class, () -> trainingService.getTrainingsByTraineeUsername(null));
    }

    @Test
    public void validateDuration_shouldThrowBadRequestException_whenDurationIsNotNumber() {
        assertThrows(BadRequestException.class, () -> trainingService.validateDuration("not a number"));
    }

    @Test
    public void validateDuration_shouldThrowBadRequestExceptionWithCorrectMessage_whenDurationIsNotNumber() {
        Exception exception = assertThrows(BadRequestException.class, () -> trainingService.validateDuration("not a number"));
        assertEquals("Duration must be a number", exception.getMessage());
    }
}
