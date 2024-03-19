package com.epam.microservice.controller;

import com.epam.microservice.Controller.TrainingWorkController;
import com.epam.microservice.model.TrainingRequest;
import com.epam.microservice.service.TrainingWorkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class TrainingWorkControllerTest {
    @InjectMocks
    private TrainingWorkController trainingWorkController;
    @Mock
    private TrainingWorkService trainingWorkService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void actionTraining() {
        // Arrange
        TrainingRequest trainingRequest = TrainingRequest.builder()
                .username("Username")
                .date(new Date())
                .action("")
                .build();

        // Act
        ResponseEntity<String> response = trainingWorkController.actionTraining(trainingRequest);

        // Assert
        verify(trainingWorkService, times(1)).acceptTrainerWork(any(TrainingRequest.class));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Action Completed successfully", response.getBody());
    }
}
