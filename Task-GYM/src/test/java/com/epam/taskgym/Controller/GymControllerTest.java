package com.epam.taskgym.Controller;

import com.epam.taskgym.controller.GymController;
import com.epam.taskgym.entity.Trainee;
import com.epam.taskgym.entity.Trainer;
import com.epam.taskgym.entity.TrainingType;
import com.epam.taskgym.entity.User;
import com.epam.taskgym.helpers.Builders;
import com.epam.taskgym.models.*;
import com.epam.taskgym.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;


public class GymControllerTest {

    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainerService trainerService;
    @Mock
    private TrainingTypeService trainingTypeService;
    @Mock
    private TrainingService trainingService;
    @Mock
    private UserService userService;
    @Mock
    private AuthService authService;

    @InjectMocks
    private GymController gymController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerTrainee_ShouldReturnCreated() {
        // Arrange
        TraineeDTO traineeDTO = new TraineeDTO();
        RegisterResponse expectedResponse = new RegisterResponse("derek.ayala", "12345678");

        given(traineeService.registerTrainee(any(TraineeDTO.class))).willReturn(expectedResponse);

        // Act
        ResponseEntity<RegisterResponse> responseEntity = gymController.registerTrainee(traineeDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode(), "The response status should be CREATED.");
        assertSame(expectedResponse, responseEntity.getBody(), "The response body should match the expected response.");
    }

    @Test
    void registerTrainer_ShouldReturnCreated() {
        TrainerDTO trainerDTO = new TrainerDTO();
        RegisterResponse expectedResponse = new RegisterResponse("derek.ayala", "12345678");

        given(trainerService.registerTrainer(any(TrainerDTO.class))).willReturn(expectedResponse);

        ResponseEntity<RegisterResponse> responseEntity = gymController.registerTrainer(trainerDTO);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertSame(expectedResponse, responseEntity.getBody());
    }

    @Test
    void loginUser_ShouldReturnOk() {
        LoginRequest loginRequest = LoginRequest.builder().username("username").password("password").build();
        LoginResponse expectedResponse = LoginResponse.builder().accessToken("").build();

        given(authService.attemptLogin(anyString(), anyString())).willReturn(expectedResponse);

        ResponseEntity<LoginResponse> responseEntity = gymController.loginUser(loginRequest);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertSame(expectedResponse, responseEntity.getBody());
    }

    @Test
    void updatePassword_ShouldReturnOk() {
        String username = "user";
        String newPassword = "newPass";
        RegisterResponse registerResponse = new RegisterResponse("user","newPass");

        given(userService.updatePassword(username, newPassword)).willReturn(registerResponse);

        ResponseEntity<RegisterResponse> responseEntity = gymController.updatePassword(username, newPassword);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertSame(registerResponse, responseEntity.getBody());
    }

    @Test
    void getTraineeProfile_ShouldReturnOk() {
        String username = "trainee";
        Trainee trainee = new Trainee();
        TraineeDTO expectedDTO = new TraineeDTO();

        when(traineeService.getTraineeByUsername(username)).thenReturn(trainee);

        try (MockedStatic<Builders> mockedBuilders = mockStatic(Builders.class)) {
            mockedBuilders.when(() -> Builders.convertTraineeToTraineeDTO(trainee)).thenReturn(expectedDTO);

            ResponseEntity<TraineeDTO> responseEntity = gymController.getTraineeProfile(username);

            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            assertSame(expectedDTO, responseEntity.getBody());
        }
    }

    @Test
    void updateTraineeProfile_ShouldReturnOk() {
        String username = "trainee";
        TraineeDTO traineeDTO = new TraineeDTO();

        Trainee trainee = new Trainee();
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        trainee.setUser(user);

        trainee.setTrainers(new ArrayList<>());

        TraineeDTO expectedDTO = new TraineeDTO();

        try (MockedStatic<Builders> mockedBuilders = Mockito.mockStatic(Builders.class)) {
            mockedBuilders.when(() -> Builders.convertTraineeToTraineeDTO(any(Trainee.class))).thenReturn(expectedDTO);

            when(traineeService.updateTrainee(any(TraineeDTO.class), anyString())).thenReturn(trainee);

            ResponseEntity<TraineeDTO> responseEntity = gymController.updateTraineeProfile(username, traineeDTO);

            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            assertSame(expectedDTO, responseEntity.getBody());
        }
    }

    @Test
    void deleteTraineeProfile_ShouldReturnOk() {
        String username = "trainee";

        doNothing().when(traineeService).deleteTrainee(username);

        ResponseEntity<String> responseEntity = gymController.deleteTraineeProfile(username);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Trainee profile deleted successfully", responseEntity.getBody());
    }

    @Test
    void getTrainerProfile_ShouldReturnOk() {
        String username = "trainer";
        Trainer trainer = new Trainer();
        TrainerDTO expectedDTO = new TrainerDTO();

        when(trainerService.getTrainerByUsername(username)).thenReturn(trainer);

        try (MockedStatic<Builders> mockedBuilders = mockStatic(Builders.class)) {
            mockedBuilders.when(() -> Builders.convertTrainerToTraineeDTO(trainer)).thenReturn(expectedDTO);

            ResponseEntity<TrainerDTO> responseEntity = gymController.getTrainerProfile(username);

            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            assertSame(expectedDTO, responseEntity.getBody());
        }
    }

    @Test
    void updateTrainerProfile_ShouldReturnOk() {
        String username = "trainer";
        TrainerDTO trainerDTO = new TrainerDTO();
        Trainer trainer = new Trainer();
        TrainerDTO expectedDTO = new TrainerDTO();

        when(trainerService.updateTrainer(trainerDTO, username)).thenReturn(trainer);

        try (MockedStatic<Builders> mockedBuilders = Mockito.mockStatic(Builders.class)) {
            mockedBuilders.when(() -> Builders.convertTrainerToTraineeDTO(any(Trainer.class))).thenReturn(expectedDTO);

            ResponseEntity<TrainerDTO> responseEntity = gymController.updateTrainerProfile(username, trainerDTO);

            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            assertSame(expectedDTO, responseEntity.getBody());
        }
    }

    @Test
    void getNotAssignedTrainers_ShouldReturnOk() {
        String username = "traineeUsername";
        List<TrainerListItem> expectedList = new ArrayList<>();

        when(trainerService.getUnassignedTrainers(anyString())).thenReturn(expectedList);

        ResponseEntity<List<TrainerListItem>> responseEntity = gymController.getNotAssignedTrainers(username);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedList, responseEntity.getBody());
    }

    @Test
    void updateTraineeTrainers_ShouldReturnOk() {
        String username = "trainee";
        List<String> trainerUsernames = List.of("trainer1", "trainer2");
        List<TrainerListItem> updatedTrainersList = new ArrayList<>();

        when(traineeService.updateTrainersList(username, trainerUsernames)).thenReturn(updatedTrainersList);

        ResponseEntity<List<TrainerListItem>> responseEntity = gymController.updateTraineeTrainers(username, trainerUsernames);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(updatedTrainersList, responseEntity.getBody());
    }

    @Test
    void getTraineeTrainings_ShouldReturnOk() {
        String username = "traineeUsername";
        TrainingFilteredDTO trainingFilteredDTO = new TrainingFilteredDTO();
        List<TrainingResponse> expectedTrainingsList = new ArrayList<>();

        when(trainingService.getTraineeTrainingsFiltered(any(TrainingFilteredDTO.class))).thenReturn(expectedTrainingsList);

        ResponseEntity<List<TrainingResponse>> responseEntity = gymController.getTraineeTrainings(username, trainingFilteredDTO);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedTrainingsList, responseEntity.getBody());
    }

    @Test
    void getTrainerTrainings_ShouldReturnOk() {
        String username = "trainerUsername";
        TrainingFilteredDTO trainingFilteredDTO = new TrainingFilteredDTO();
        List<TrainingResponse> expectedTrainingsList = new ArrayList<>();

        when(trainingService.getTrainerTrainingsFiltered(any(TrainingFilteredDTO.class))).thenReturn(expectedTrainingsList);

        ResponseEntity<List<TrainingResponse>> responseEntity = gymController.getTrainerTrainings(username, trainingFilteredDTO);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedTrainingsList, responseEntity.getBody());
    }

    @Test
    void addTraining_ShouldReturnCreated() {
        TrainingDTO trainingDTO = new TrainingDTO();
        TrainingDTO trainingDTOResponse = new TrainingDTO();

        when(trainingService.createTraining(any(TrainingDTO.class))).thenReturn(trainingDTOResponse);

        ResponseEntity<TrainingDTO> responseEntity = gymController.addTraining(trainingDTO);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(trainingDTOResponse, responseEntity.getBody());
    }

    @Test
    void activateDeactivateTrainee_ShouldReturnOk() {
        String username = "traineeUsername";
        boolean isActive = true;
        TraineeDTO expectedDTO = new TraineeDTO();

        when(traineeService.ActivateDeactivateTrainee(username, isActive)).thenReturn(expectedDTO);

        ResponseEntity<TraineeDTO> responseEntity = gymController.activateDeactivateTrainee(username, isActive);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedDTO, responseEntity.getBody());
    }

    @Test
    void activateDeactivateTrainer_ShouldReturnOk() {
        String username = "trainerUsername";
        boolean isActive = false;
        TrainerDTO expectedDTO = new TrainerDTO();

        when(trainerService.ActivateDeactivateTrainer(username, isActive)).thenReturn(expectedDTO);

        ResponseEntity<TrainerDTO> responseEntity = gymController.activateDeactivateTrainer(username, isActive);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedDTO, responseEntity.getBody());
    }

    @Test
    void getAllTrainingTypes_ShouldReturnOk() {
        List<TrainingType> expectedTypesList = new ArrayList<>();

        when(trainingTypeService.getAllTrainingTypes()).thenReturn(expectedTypesList);

        ResponseEntity<List<TrainingType>> responseEntity = gymController.getAllTrainingTypes();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedTypesList, responseEntity.getBody());
    }
}