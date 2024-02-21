package com.epam.taskgym;

import com.epam.taskgym.controller.helpers.TrainerDetails;
import com.epam.taskgym.entity.Trainer;
import com.epam.taskgym.entity.TrainingType;
import com.epam.taskgym.entity.User;
import com.epam.taskgym.repository.TrainerRepository;
import com.epam.taskgym.service.TrainerService;
import com.epam.taskgym.service.TrainingTypeService;
import com.epam.taskgym.service.UserService;
import com.epam.taskgym.exception.MissingAttributes;
import com.epam.taskgym.exception.NotFoundException;
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
    }

    @Test
    void getTrainerByUsername_whenTrainerExists_shouldReturnTrainer() {
        when(trainerRepository.findByUserUsername(anyString())).thenReturn(Optional.of(trainer));
        Trainer result = trainerService.getTrainerByUsername("john.doe");
        assertEquals(trainer, result);
    }

    @Test
    void getTrainerByUsername_whenTrainerDoesNotExist_shouldThrowException() {
        when(trainerRepository.findByUserUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            trainerService.getTrainerByUsername("john.doe");
        });
    }

    @Test
    void registerTrainer_whenDetailsAreValid_shouldReturnRegisteredTrainer() {
        TrainerDetails trainerDetails = new TrainerDetails();
        trainerDetails.setFirstName("John");
        trainerDetails.setLastName("Doe");
        trainerDetails.setSpecialization("TrainingType1");

        when(userService.createUser(trainerDetails.getFirstName(), trainerDetails.getLastName())).thenReturn(user);
        when(trainingTypeService.getTrainingTypeByName(anyString())).thenReturn(trainingType);
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);

        Trainer result = trainerService.registerTrainer(trainerDetails);

        assertEquals("John", result.getUser().getFirstName());
        assertEquals("Doe", result.getUser().getLastName());
        assertEquals("TrainingType1", result.getSpecialization().getName());
    }

    @Test
    void registerTrainer_whenDetailsAreInvalid_shouldThrowException() {
        TrainerDetails invalidTrainerDetails = new TrainerDetails();

        assertThrows(MissingAttributes.class, () -> {
            trainerService.registerTrainer(invalidTrainerDetails);
        });
    }

    @Test
    void updateTrainer_whenDetailsAreValid_shouldUpdateTrainer() {
        TrainerDetails trainerDetails = new TrainerDetails();
        trainerDetails.setFirstName("Jack");
        trainerDetails.setLastName("Daniel");
        trainerDetails.setSpecialization("TrainingType1");

        user.setFirstName("Jack");
        user.setLastName("Daniel");

        when(userService.authenticateUser(anyString(), anyString())).thenReturn(user);
        when(trainerRepository.findByUserUsername(anyString())).thenReturn(Optional.of(trainer));
        when(userService.updateUser(trainerDetails.getFirstName(), trainerDetails.getLastName(), user)).thenReturn(user);
        when(trainingTypeService.getTrainingTypeByName(anyString())).thenReturn(new TrainingType());
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);

        Trainer result = trainerService.updateTrainer(trainerDetails, "john.doe", "password");

        assertEquals("Jack", result.getUser().getFirstName());
        assertEquals("Daniel", result.getUser().getLastName());
    }
}
