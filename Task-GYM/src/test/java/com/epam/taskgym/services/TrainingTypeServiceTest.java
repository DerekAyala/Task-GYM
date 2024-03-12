package com.epam.taskgym.services;

import com.epam.taskgym.entity.TrainingType;
import com.epam.taskgym.repository.TrainingTypeRepository;
import com.epam.taskgym.service.TrainingTypeService;
import com.epam.taskgym.exception.MissingAttributes;
import com.epam.taskgym.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingTypeServiceTest {

    @InjectMocks
    private TrainingTypeService trainingTypeService;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    private TrainingType trainingType;

    @BeforeEach
    public void setUp() {
        trainingType = new TrainingType();
        trainingType.setName("TrainingType1");
    }

    @Test
    void getAllTrainingTypesTest() {
        when(trainingTypeRepository.findAll()).thenReturn(Arrays.asList(trainingType));

        List<TrainingType> trainingTypes = trainingTypeService.getAllTrainingTypes();

        assertEquals(1 , trainingTypes.size());
        assertEquals("TrainingType1", trainingTypes.get(0).getName());
    }

    @Test
    void registerTrainingType_whenNameIsValid_shouldRegisterTrainingType() {
        when(trainingTypeRepository.save(any(TrainingType.class))).thenReturn(trainingType);

        TrainingType result = trainingTypeService.RegisterTrainingType("TrainingType1");

        assertEquals("TrainingType1", result.getName());
    }

    @Test
    void registerTrainingType_whenNameIsEmpty_shouldThrowException() {
        assertThrows(MissingAttributes.class, () -> {
            trainingTypeService.RegisterTrainingType("");
        });
    }

    @Test
    void getTrainingTypeByNameTest_whenNameExists_shouldReturnTrainingType() {
        when(trainingTypeRepository.findByName(anyString())).thenReturn(Optional.of(trainingType));

        TrainingType foundTrainingType = trainingTypeService.getTrainingTypeByName("TrainingType1");

        assertEquals("TrainingType1", foundTrainingType.getName());
    }

    @Test
    void getTrainingTypeByNameTest_whenNameDoesNotExists_shouldThrowNotFoundException() {
        when(trainingTypeRepository.findByName(anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            trainingTypeService.getTrainingTypeByName("TrainingType1");
        });
    }
}
