package com.epam.taskgym;

import com.epam.taskgym.controller.helpers.TraineeDetails;
import com.epam.taskgym.entity.Trainee;
import com.epam.taskgym.entity.User;
import com.epam.taskgym.exception.*;
import com.epam.taskgym.repository.TraineeRepository;
import com.epam.taskgym.repository.TrainingRepository;
import com.epam.taskgym.service.TraineeService;
import com.epam.taskgym.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// Required imports
@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @InjectMocks
    TraineeService traineeService;
    @Mock
    TraineeRepository traineeRepository;
    @Mock
    UserService userService;
    @Mock
    TrainingRepository trainingRepository;

    User user;
    Trainee trainee;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("john.doe");
        user.setPassword("password");

        trainee = new Trainee();
        trainee.setUser(user);
    }

    @Test
    void getTraineeByUsername_whenTraineeExists_shouldReturnTrainee() {
        when(traineeRepository.findByUserUsername(anyString())).thenReturn(Optional.of(trainee));
        Trainee result = traineeService.getTraineeByUsername("john.doe");
        assertEquals(trainee, result);
    }

    @Test
    void getTraineeByUsername_whenTraineeDoesNotExist_shouldThrowException() {
        when(traineeRepository.findByUserUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            traineeService.getTraineeByUsername("john.doe");
        });
    }

    @Test
    void registerTrainee_whenDetailsAreValid_shouldReturnCreatedTrainee() {
        TraineeDetails traineeDetails = new TraineeDetails();
        traineeDetails.setFirstName("John");
        traineeDetails.setLastName("Doe");
        when(userService.createUser(traineeDetails.getFirstName(), traineeDetails.getLastName())).thenReturn(user);
        when(traineeRepository.save(any(Trainee.class))).thenReturn(trainee);

        Trainee result = traineeService.registerTrainee(traineeDetails);
        assertEquals("John", result.getUser().getFirstName());
        assertEquals("Doe", result.getUser().getLastName());
    }

    @Test
    void registerTrainee_whenDetailsAreInvalid_shouldThrowException() {
        TraineeDetails invalidTraineeDetails = new TraineeDetails();

        assertThrows(MissingAttributes.class, () -> {
            traineeService.registerTrainee(invalidTraineeDetails);
        });
    }

    @Test
    void updateTrainee_whenUsernameAndPasswordAreCorrect_shouldUpdateTrainee() {
        TraineeDetails traineeDetails = new TraineeDetails();
        traineeDetails.setFirstName("John");
        traineeDetails.setLastName("Doe");

        when(traineeRepository.findByUserUsername(anyString())).thenReturn(Optional.of(trainee));
        when(userService.authenticateUser(anyString(), anyString())).thenReturn(user);
        when(userService.updateUser(traineeDetails.getFirstName(), traineeDetails.getLastName(),user)).thenReturn(user);
        when(traineeRepository.save(any(Trainee.class))).thenReturn(trainee);

        Trainee result = traineeService.updateTrainee(traineeDetails, "john.doe", "password");

        assertEquals("John", result.getUser().getFirstName());
        assertEquals("Doe", result.getUser().getLastName());
    }

    @Test
    void updateTrainee_whenUsernameOrPasswordIsIncorrect_shouldThrowException() {
        Trainee incorrectPasswordTrainee = new Trainee();
        User incorrectPasswordUser = new User();
        incorrectPasswordUser.setPassword("not the right password");
        incorrectPasswordTrainee.setUser(incorrectPasswordUser);

        when(traineeRepository.findByUserUsername(anyString())).thenReturn(Optional.of(incorrectPasswordTrainee));

        assertThrows(FailAuthenticateException.class, () -> {
            traineeService.updateTrainee(new TraineeDetails(), "john.doe", "password");
        });
    }

    @Test
    void deleteTrainee_whenTraineeExists_shouldNotThrowException() {
        when(traineeRepository.findByUserUsername(anyString())).thenReturn(Optional.of(trainee));
        when(userService.authenticateUser(anyString(), anyString())).thenReturn(user);
        assertDoesNotThrow(() -> {
            traineeService.deleteTrainee("john.doe", "password");
        });
        verify(userService, times(1)).deleteUser(user);
        verify(traineeRepository, times(1)).delete(trainee);
        verify(trainingRepository, times(1)).deleteAllByTrainee_User_Username("john.doe");
    }

    @Test
    void deleteTrainee_whenTraineeDoesNotExist_shouldThrowException() {
        when(traineeRepository.findByUserUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            traineeService.deleteTrainee("john.doe", "password");
        });
    }

    @Test
    void validateDate_whenDateFormatIsValid_shouldNotThrowException() {
        assertDoesNotThrow(() -> {
            traineeService.validateDate("12-12-2022");
        });
    }

    @Test
    void validateDate_whenDateFormatIsInvalid_shouldThrowException() {
        assertThrows(BadRequestException.class, () -> {
            traineeService.validateDate("invalid date format");
        });
    }
}
