package com.epam.microservice.service;

import com.epam.microservice.entity.TrainingMonth;
import com.epam.microservice.entity.TrainingWork;
import com.epam.microservice.entity.TrainingYears;
import com.epam.microservice.exception.MissingAttributes;
import com.epam.microservice.exception.NotFoundException;
import com.epam.microservice.model.TrainingRequest;
import com.epam.microservice.repository.TrainingMonthRepository;
import com.epam.microservice.repository.TrainingWorkRepository;
import com.epam.microservice.repository.TrainingYearsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TrainingWorkServiceTest {

    @InjectMocks
    private TrainingWorkService trainingWorkService;
    @Mock
    private TrainingWorkRepository trainingWorkRepository;
    @Mock
    private TrainingYearsRepository trainingYearsRepository;
    @Mock
    private TrainingMonthRepository trainingMonthRepository;

    TrainingWork trainingWork;
    TrainingYears trainingYears;
    TrainingMonth trainingMonth;

    TrainingRequest trainingRequest;

    @BeforeEach
    public void setup() {
        trainingWork = new TrainingWork();
        trainingWork.setFirstName("John");
        trainingWork.setLastName("Doe");
        trainingWork.setUsername("john.doe");
        trainingWork.setStatus(true);
        trainingWork.setId(1L);

        trainingYears = new TrainingYears();
        trainingYears.setId(1L);
        trainingYears.setYearNumber("2021");

        trainingMonth = new TrainingMonth();
        trainingMonth.setId(1L);
        trainingMonth.setMonthName("January");
        trainingMonth.setHours(10);

        // Use ArrayList instead of List.of
        trainingYears.setMonths(new ArrayList<>(Arrays.asList(trainingMonth)));
        trainingWork.setYears(new ArrayList<>(Arrays.asList(trainingYears)));

        LocalDate localDate = LocalDate.of(2021, 1, 20);
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        trainingRequest = TrainingRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .username("john.doe")
                .date(date)
                .isActive(true)
                .action("add")
                .build();
    }

    @Test
    public void testAcceptTrainerWork_NullTrainingRequest() {
        assertThrows(MissingAttributes.class, () -> trainingWorkService.acceptTrainerWork(null), "Training request is required");
    }

    @Test
    public void testAcceptTrainerWork_NullAction() {
        //create a new TrainingRequest with null action
        TrainingRequest trainingRequest = TrainingRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .username("john.doe")
                .date(new Date())
                .isActive(true)
                .build();

        assertThrows(MissingAttributes.class, () -> trainingWorkService.acceptTrainerWork(trainingRequest), "Action is required");
    }

    @Test
    public void testAcceptTrainerWork_InvalidAction() {
        //create a new TrainingRequest with invalid action
        TrainingRequest trainingRequest = TrainingRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .username("john.doe")
                .date(new Date())
                .isActive(true)
                .action("invalid")
                .build();

        assertThrows(MissingAttributes.class, () -> trainingWorkService.acceptTrainerWork(trainingRequest), "Action must be add or delete");
    }

    @Test
    public void testAddTrainingWork_NullTrainingRequestValue() {
        //Create a new TrainingRequest with null firstName
        TrainingRequest trainingRequest = TrainingRequest.builder()
                .firstName(null) // null FirstName
                .lastName("Doe")
                .username("john.doe")
                .date(new Date())
                .isActive(true)
                .build();

        assertThrows(MissingAttributes.class, () ->
                        trainingWorkService.addTrainingWork(trainingRequest),
                "First name, last name, username, date and status are required fields");
    }

    @Test
    public void testAddTrainingWork_CreateTrainingWork() {
        // Training request with all attributes present
        given(trainingWorkRepository.findByUsername(anyString()))
                .willReturn(Optional.empty());

        assertDoesNotThrow(() -> trainingWorkService.addTrainingWork(trainingRequest));

        // Verify that work repository was called
        verify(trainingWorkRepository, times(1)).save(any());
    }

    @Test
    public void testAddTrainingWork_UpdateTrainingWork() {
        // Training request with all attributes present
        given(trainingWorkRepository.findByUsername(anyString()))
                .willReturn(Optional.of(trainingWork)); // Existing training work

        assertDoesNotThrow(() -> trainingWorkService.addTrainingWork(trainingRequest));

        // Verify that work repository was called
        verify(trainingWorkRepository, times(1)).save(any());
    }

    @Test
    public void testDeleteTrainingWork_TrainingWorkNotFound() {
        // TrainingRequest with username
        TrainingRequest request = TrainingRequest.builder()
                .username("notfound")
                .date(new Date())
                .build();

        given(trainingWorkRepository.findByUsername(anyString())).willReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> trainingWorkService.deleteTrainingWork(request),
                "Training work not found");
    }

    @Test
    public void testDeleteTrainingWork_TrainingYearsNotFound() {
        TrainingRequest request = TrainingRequest.builder()
                .username("john.doe")
                .date(new Date())
                .build();

        TrainingWork work = new TrainingWork();
        work.setYears(new ArrayList<>());  // Years list is empty

        given(trainingWorkRepository.findByUsername(anyString())).willReturn(Optional.of(work));

        assertDoesNotThrow(() -> trainingWorkService.deleteTrainingWork(request));
        verify(trainingWorkRepository, times(1)).delete(any());  // Check TrainingWork is deleted
    }
}
