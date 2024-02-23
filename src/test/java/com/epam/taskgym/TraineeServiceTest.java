package com.epam.taskgym;

import com.epam.taskgym.dto.TraineeDTO;
import com.epam.taskgym.entity.Trainee;
import com.epam.taskgym.entity.Trainer;
import com.epam.taskgym.entity.User;
import com.epam.taskgym.exception.*;
import com.epam.taskgym.repository.TraineeRepository;
import com.epam.taskgym.repository.TrainerRepository;
import com.epam.taskgym.repository.TrainingRepository;
import com.epam.taskgym.service.TraineeService;
import com.epam.taskgym.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
    @Mock
    TrainerRepository trainerRepository;

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
    void testGetTraineeByUsername() {
        when(traineeRepository.findByUserUsername(user.getUsername())).thenReturn(Optional.of(trainee));

        Trainee result = traineeService.getTraineeByUsername(user.getUsername());

        assertEquals(trainee, result);
    }

    @Test
    void testGetTraineeByUsername_NotFound() {
        when(traineeRepository.findByUserUsername("unknown.username")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> traineeService.getTraineeByUsername("unknown.username"));
    }

    @Test
    void testRegisterTrainee() {
        TraineeDTO traineeDTO = new TraineeDTO();
        traineeDTO.setFirstName("John");
        traineeDTO.setLastName("Doe");
        traineeDTO.setDateOfBirth(new Date());

        when(userService.createUser(traineeDTO.getFirstName(), traineeDTO.getLastName())).thenReturn(user);
        when(traineeRepository.save(any(Trainee.class))).thenAnswer(i -> i.getArguments()[0]);

        Trainee newTrainee = traineeService.registerTrainee(traineeDTO);

        assertEquals(traineeDTO.getFirstName(), newTrainee.getUser().getFirstName());
        assertEquals(traineeDTO.getLastName(), newTrainee.getUser().getLastName());

        verify(userService, times(1)).createUser(traineeDTO.getFirstName(), traineeDTO.getLastName());
        verify(traineeRepository, times(1)).save(any(Trainee.class));
    }

    @Test
    void testRegisterTrainee_NullTraineeDTO() {
        assertThrows(MissingAttributes.class, () -> traineeService.registerTrainee(null));
    }

    @Test
    void testUpdateTrainee_AuthenticationPasses() {
        TraineeDTO traineeDTO = new TraineeDTO();
        traineeDTO.setFirstName("John");
        traineeDTO.setLastName("Doe");

        when(userService.authenticateUser(any(String.class), any(String.class))).thenReturn(user);
        when(traineeRepository.findByUserUsername(any(String.class))).thenReturn(Optional.of(trainee));
        when(userService.updateUser(any(String.class), any(String.class), any(User.class))).thenReturn(user);
        when(traineeRepository.save(any(Trainee.class))).thenAnswer(i -> i.getArguments()[0]);

        Trainee updatedTrainee = traineeService.updateTrainee(traineeDTO, user.getUsername(), user.getPassword());

        assertEquals("John", updatedTrainee.getUser().getFirstName());
        assertEquals("Doe", updatedTrainee.getUser().getLastName());
        verify(userService, times(1)).authenticateUser(any(String.class), any(String.class));
        verify(userService, times(1)).updateUser(any(String.class), any(String.class), any(User.class));
        verify(traineeRepository, times(2)).findByUserUsername(user.getUsername());
        verify(traineeRepository, times(1)).save(any(Trainee.class));
    }

    @Test
    void testUpdateTrainee_AuthenticationFails() {
        TraineeDTO traineeDTO = new TraineeDTO();
        traineeDTO.setFirstName("Updated");
        traineeDTO.setLastName("User");

        String wrongPassword = "wrongPassword";

        when(userService.authenticateUser(any(String.class), any(String.class))).thenThrow(FailAuthenticateException.class);
        assertThrows(FailAuthenticateException.class, () -> traineeService.updateTrainee(traineeDTO, user.getUsername(), wrongPassword));
        verify(userService, times(1)).authenticateUser(any(String.class), any(String.class));
        verify(userService, times(0)).updateUser(any(String.class), any(String.class), any(User.class));
        verify(traineeRepository, times(0)).findByUserUsername(user.getUsername());
        verify(traineeRepository, times(0)).save(any(Trainee.class));
    }

    @Test
    void testDeleteTrainee() {
        String username = "username";
        String password = "password";

        when(userService.authenticateUser(username, password)).thenReturn(user);
        when(traineeRepository.findByUserUsername(username)).thenReturn(Optional.of(trainee));
        when(trainingRepository.findAllTrainersByTraineeUsername(username)).thenReturn(new ArrayList<Trainer>());

        assertDoesNotThrow(() -> traineeService.deleteTrainee(username, password));

        verify(userService, times(1)).authenticateUser(username, password);
        verify(traineeRepository, times(2)).findByUserUsername(username);
        verify(trainingRepository, times(1)).findAllTrainersByTraineeUsername(username);
        verify(trainingRepository, times(1)).deleteAllByTrainee_User_Username(username);
        verify(traineeRepository, times(1)).delete(trainee);
        verify(userService, times(1)).deleteUser(user);
    }

}
