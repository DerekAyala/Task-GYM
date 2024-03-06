package com.epam.taskgym;

import com.epam.taskgym.models.TraineeDTO;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
        trainee.setTrainers(new ArrayList<Trainer>());
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
    void testRegisterTrainee_NullTraineeDTO() {
        assertThrows(MissingAttributes.class, () -> traineeService.registerTrainee(null));
    }
}
