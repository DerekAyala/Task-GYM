package com.epam.taskgym.services;

import com.epam.taskgym.entity.Trainer;
import com.epam.taskgym.entity.TrainingType;
import com.epam.taskgym.helpers.Builders;
import com.epam.taskgym.models.RegisterResponse;
import com.epam.taskgym.models.TraineeDTO;
import com.epam.taskgym.entity.Trainee;
import com.epam.taskgym.entity.User;
import com.epam.taskgym.exception.*;
import com.epam.taskgym.models.TrainerListItem;
import com.epam.taskgym.models.UserResponse;
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
import static org.mockito.BDDMockito.given;
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
        trainee.setTrainers(new ArrayList<>());
    }

    @Test
    void testGetTraineeByUsername() {
        given(traineeRepository.findByUserUsername(user.getUsername())).willReturn(Optional.of(trainee));

        Trainee result = traineeService.getTraineeByUsername(user.getUsername());

        assertEquals(trainee, result);
    }

    @Test
    void testGetTraineeByUsername_NotFound() {
        given(traineeRepository.findByUserUsername("unknown.username")).willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> traineeService.getTraineeByUsername("unknown.username"));
    }

    @Test
    void testRegisterTrainee_NullTraineeDTO() {
        assertThrows(MissingAttributes.class, () -> traineeService.registerTrainee(null));
    }

    @Test
    public void givenValidTraineeDTO_whenRegisterTrainee_thenCreatedTraineeReturned() {
        TraineeDTO traineeDTO = new TraineeDTO();
        traineeDTO.setFirstName("John");
        traineeDTO.setLastName("Doe");

        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("John.Doe");
        user.setIsActive(true);
        user.setRole("ROLE_TRAINEE");

        UserResponse userResponse = new UserResponse(user, "password");

        given(userService.createUser(traineeDTO.getFirstName(), traineeDTO.getLastName(), "ROLE_TRAINEE"))
        .willReturn(userResponse);

        Trainee trainee = new Trainee();
        trainee.setUser(user);

        given(traineeRepository.save(any(Trainee.class))).willReturn(trainee);

        RegisterResponse registerResponse = traineeService.registerTrainee(traineeDTO);

        assertEquals(user.getUsername(), registerResponse.getUsername());
        verify(traineeRepository).save(any(Trainee.class));
        verify(userService).createUser(traineeDTO.getFirstName(), traineeDTO.getLastName(), "ROLE_TRAINEE");
    }

    @Test
    public void shouldUpdateTrainee() {
        TraineeDTO traineeDTO = new TraineeDTO();
        traineeDTO.setFirstName("UpdatedJohn");
        traineeDTO.setLastName("UpdatedDoe");
        given(traineeRepository.findByUserUsername(user.getUsername())).willReturn(Optional.of(trainee));
        given(userService.updateUser(traineeDTO.getFirstName(), traineeDTO.getLastName(), trainee.getUser())).willReturn(user);
        user.setFirstName("UpdatedJohn");
        user.setLastName("UpdatedDoe");
        Trainee updatedTrainee = traineeService.updateTrainee(traineeDTO, user.getUsername());

        assertEquals(traineeDTO.getFirstName(), updatedTrainee.getUser().getFirstName());
        assertEquals(traineeDTO.getLastName(), updatedTrainee.getUser().getLastName());
        verify(traineeRepository).save(trainee);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenUpdateNonExistentTrainee() {
        TraineeDTO traineeDTO = new TraineeDTO();
        traineeDTO.setFirstName("UpdatedJohn");
        traineeDTO.setLastName("UpdatedDoe");
        given(traineeRepository.findByUserUsername("unknown.username")).willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> traineeService.updateTrainee(traineeDTO, "unknown.username"));
    }

    @Test
    public void givenValidUsername_whenDeleteTrainee_thenTraineeDeleted() {
        // Given
        String username = "john.doe";
        List<Trainer> trainers = new ArrayList<>();
        given(traineeRepository.findByUserUsername(username)).willReturn(Optional.of(trainee));
        given(trainingRepository.findAllTrainersByTraineeUsername(username)).willReturn(trainers);

        // When
        traineeService.deleteTrainee(username);

        // Then
        verify(traineeRepository).delete(trainee);
        verify(userService).deleteUser(trainee.getUser());
    }

    @Test
    public void givenInvalidUsername_whenDeleteTrainee_thenDeleteFailsWithNotFoundException() {
        // Given
        String username = "unknown.username";
        given(traineeRepository.findByUserUsername(username)).willReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> traineeService.deleteTrainee(username));
    }

    @Test
    public void givenValidUsernameAndIsActive_whenActivateDeactivateTrainee_thenUserActiveStatusUpdated() {
        // Given
        String username = "john.doe";
        boolean isActive = false;
        given(traineeRepository.findByUserUsername(username)).willReturn(Optional.of(trainee));
        given(userService.saveUser(user)).willReturn(user);

        // When
        TraineeDTO result = traineeService.ActivateDeactivateTrainee(username, isActive);

        // Then
        assertFalse(result.getIsActive());
        verify(traineeRepository).findByUserUsername(username);
        verify(userService).saveUser(user);
    }

    @Test
    public void givenInvalidUsername_whenActivateDeactivateTrainee_thenNotFoundExceptionThrown() {
        // Given
        String username = "unknown.username";
        boolean isActive = true;
        given(traineeRepository.findByUserUsername(username)).willReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> traineeService.ActivateDeactivateTrainee(username, isActive));
    }

    @Test
    public void givenValidUsernameAndTrainersUsernames_whenUpdateTrainersList_thenTrainersListUpdatedForTrainee() {
        // Given
        String username = "john.doe";
        String trainerUsername = "trainer.username";
        List<String> trainersUsernames = Collections.singletonList(trainerUsername);
        TrainingType trainingType = new TrainingType();
        trainingType.setName("Training");
        Trainer trainer = new Trainer();
        trainer.setTrainees(new ArrayList<>());
        trainer.setSpecialization(trainingType);
        User trainerUser = new User();
        trainerUser.setUsername(trainerUsername);
        trainer.setUser(trainerUser);
        given(traineeRepository.findByUserUsername(username)).willReturn(Optional.of(trainee));
        given(trainerRepository.findByUserUsername(trainerUsername)).willReturn(Optional.of(trainer));

        // When
        List<TrainerListItem> result = traineeService.updateTrainersList(username, trainersUsernames);
        List<TrainerListItem> expected = Builders.convertTrainersToTrainerListItem(trainee.getTrainers());

        // Then
        assertEquals(expected.size(), result.size());
        verify(traineeRepository).findByUserUsername(username);
        verify(trainerRepository).findByUserUsername(trainerUsername);
    }

    @Test
    public void givenInvalidUsername_whenUpdateTrainersList_thenNotFoundExceptionThrown() {
        // Given
        String username = "unknown.username";
        List<String> trainerUsernames = Collections.singletonList("trainer.username");
        given(traineeRepository.findByUserUsername(username)).willReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> traineeService.updateTrainersList(username, trainerUsernames));
    }
}
