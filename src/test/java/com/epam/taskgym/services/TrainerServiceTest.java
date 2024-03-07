package com.epam.taskgym.services;

import com.epam.taskgym.entity.Trainer;
import com.epam.taskgym.entity.TrainingType;
import com.epam.taskgym.entity.User;
import com.epam.taskgym.models.TrainerDTO;
import com.epam.taskgym.models.TrainerListItem;
import com.epam.taskgym.models.UserResponse;
import com.epam.taskgym.repository.TrainerRepository;
import com.epam.taskgym.repository.TrainingRepository;
import com.epam.taskgym.service.TrainerService;
import com.epam.taskgym.service.TrainingTypeService;
import com.epam.taskgym.service.UserService;
import com.epam.taskgym.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @InjectMocks
    private TrainerService trainerService;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private UserService userService;
    @Mock
    private TrainingTypeService trainingTypeService;
    @Mock
    private TrainingRepository trainingRepository;

    private Trainer trainer;
    private User user;
    private TrainingType trainingType;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("john.doe");
        user.setPassword("password");

        trainingType = new TrainingType();
        trainingType.setName("TrainingType1");

        trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(trainingType);
        trainer.setTrainees(new ArrayList<>());
    }

    @Test
    void testGetTrainerByUsername() {
        given(trainerRepository.findByUserUsername(user.getUsername())).willReturn(Optional.of(trainer));

        Trainer result = trainerService.getTrainerByUsername(user.getUsername());

        assertEquals(trainer, result);
    }

    @Test
    void testGetTrainerByUsername_NotFound() {
        given(trainerRepository.findByUserUsername("unknown.username")).willReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> trainerService.getTrainerByUsername("unknown.username"));
    }

    @Test
    public void registerTrainerShouldSaveTrainer() {
        TrainerDTO trainerDTO = new TrainerDTO();
        trainerDTO.setFirstName("John");
        trainerDTO.setLastName("Doe");
        trainerDTO.setSpecialization("TrainingType1");

        given(userService.createUser(trainerDTO.getFirstName(), trainerDTO.getLastName(), "ROLE_TRAINER"))
                .willReturn(new UserResponse(user, user.getPassword()));
        given(trainingTypeService.getTrainingTypeByName(trainerDTO.getSpecialization())).willReturn(trainingType);

        trainerService.registerTrainer(trainerDTO);

        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    public void givenTrainer_whenSaveTrainer_thenCallTrainerRepositorySave() {
        trainerService.saveTrainer(trainer);

        verify(trainerRepository).save(trainer);
    }

    @Test
    public void givenValidUsernameAndTrainerDTO_whenUpdateTrainer_thenTrainerUpdated() {
        TrainerDTO trainerDTO = new TrainerDTO();
        trainerDTO.setFirstName("UpdatedJohn");
        trainerDTO.setLastName("UpdatedDoe");
        trainerDTO.setSpecialization("UpdatedSpecialization");

        given(trainerRepository.findByUserUsername(user.getUsername())).willReturn(Optional.of(trainer));
        given(userService.updateUser(trainerDTO.getFirstName(), trainerDTO.getLastName(), trainer.getUser())).willReturn(user);
        given(trainingTypeService.getTrainingTypeByName(trainerDTO.getSpecialization())).willReturn(trainingType);
        user.setFirstName(trainerDTO.getFirstName());
        user.setLastName(trainerDTO.getLastName());
        trainingType.setName(trainerDTO.getSpecialization());
        Trainer updatedTrainer = trainerService.updateTrainer(trainerDTO, user.getUsername());

        assertEquals(trainerDTO.getFirstName(), updatedTrainer.getUser().getFirstName());
        assertEquals(trainerDTO.getLastName(), updatedTrainer.getUser().getLastName());
        assertEquals(trainerDTO.getSpecialization(), updatedTrainer.getSpecialization().getName());
        verify(trainerRepository).save(updatedTrainer);
    }

    @Test
    public void givenValidUsernameAndIsActive_whenActivateDeactivateTrainer_thenTrainerActiveStatusUpdated() {
        // Given
        String username = "john.doe";
        boolean isActive = false;
        given(trainerRepository.findByUserUsername(username)).willReturn(Optional.of(trainer));
        given(userService.saveUser(user)).willReturn(user);

        // When
        TrainerDTO result = trainerService.ActivateDeactivateTrainer(username, isActive);

        // Then
        assertFalse(result.isActive());
        verify(trainerRepository).findByUserUsername(username);
        verify(userService).saveUser(user);
    }

    @Test
    public void givenInvalidUsername_whenActivateDeactivateTrainer_thenNotFoundExceptionThrown() {
        // Given
        String username = "unknown.username";
        boolean isActive = true;
        given(trainerRepository.findByUserUsername(username)).willReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> trainerService.ActivateDeactivateTrainer(username, isActive));
    }

    @Test
    public void givenTraineeUsername_whenGetUnassignedTrainers_thenReturnTrainersNotAssignedForTrainee() {
        // Given
        String traineeUsername = "john.doe";
        User trainerUser = new User();
        trainerUser.setFirstName("John");
        trainerUser.setLastName("Doe");

        TrainingType trainingType = new TrainingType();
        trainingType.setName("SampleTraining");

        Trainer trainer1 = new Trainer();
        trainer1.setUser(trainerUser);
        trainer1.setSpecialization(trainingType);

        Trainer trainer2 = new Trainer();
        trainer2.setUser(trainerUser);
        trainer2.setSpecialization(trainingType);

        Trainer trainer3 = new Trainer();
        trainer3.setUser(trainerUser);
        trainer3.setSpecialization(trainingType);

        List<Trainer> allTrainers = Arrays.asList(trainer1, trainer2, trainer3);
        List<Trainer> assignedTrainers = Arrays.asList(trainer1, trainer2);

        given(trainerRepository.findAll()).willAnswer(invocation -> new ArrayList<>(allTrainers));
        given(trainingRepository.findAllTrainersByTraineeUsername(traineeUsername)).willReturn(assignedTrainers);

        // When
        List<TrainerListItem> result = trainerService.getUnassignedTrainers(traineeUsername);

        // Then
        assertEquals(1, result.size());
        verify(trainerRepository).findAll();
        verify(trainingRepository).findAllTrainersByTraineeUsername(traineeUsername);
    }
}
