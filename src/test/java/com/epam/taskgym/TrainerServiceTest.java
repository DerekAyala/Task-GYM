package com.epam.taskgym;

import com.epam.taskgym.models.TrainerDTO;
import com.epam.taskgym.entity.Trainer;
import com.epam.taskgym.entity.TrainingType;
import com.epam.taskgym.entity.User;
import com.epam.taskgym.repository.TrainerRepository;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    void testRegisterTrainer() {
        TrainerDTO trainerDTO = new TrainerDTO();
        trainerDTO.setFirstName("John");
        trainerDTO.setLastName("Doe");
        trainerDTO.setSpecialization("TrainingType1");

        when(userService.createUser(trainerDTO.getFirstName(), trainerDTO.getLastName(), "ROLE_TRAINER")).thenReturn(user);
        when(trainingTypeService.getTrainingTypeByName(trainerDTO.getSpecialization())).thenReturn(trainingType);
        when(trainerRepository.save(any(Trainer.class))).thenAnswer(i -> i.getArguments()[0]);

        Trainer newTrainer = trainerService.registerTrainer(trainerDTO);

        assertEquals(trainerDTO.getFirstName(), newTrainer.getUser().getFirstName());
        assertEquals(trainerDTO.getLastName(), newTrainer.getUser().getLastName());
        assertEquals(trainerDTO.getSpecialization(), newTrainer.getSpecialization().getName());

        verify(userService, times(1)).createUser(trainerDTO.getFirstName(), trainerDTO.getLastName(),"ROLE_TRAINER");
        verify(trainingTypeService, times(1)).getTrainingTypeByName(trainerDTO.getSpecialization());
        verify(trainerRepository, times(1)).save(any(Trainer.class));
    }

    @Test
    void testGetTrainerByUsername() {
        when(trainerRepository.findByUserUsername(user.getUsername())).thenReturn(Optional.of(trainer));

        Trainer result = trainerService.getTrainerByUsername(user.getUsername());

        assertEquals(trainer, result);
    }

    @Test
    void testGetTrainerByUsername_NotFound() {
        when(trainerRepository.findByUserUsername("unknown.username")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> trainerService.getTrainerByUsername("unknown.username"));
    }
}
