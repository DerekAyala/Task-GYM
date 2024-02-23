package com.epam.taskgym.controller;

import com.epam.taskgym.dto.*;
import com.epam.taskgym.entity.Trainee;
import com.epam.taskgym.entity.Trainer;
import com.epam.taskgym.entity.TrainingType;
import com.epam.taskgym.entity.User;
import com.epam.taskgym.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Api(tags = "Gym Controller", description = "Endpoints for managing gym operations")
public class GymController {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingTypeService trainingTypeService;
    private final TrainingService trainingService;
    private final UserService userService;

    public GymController(TraineeService traineeService, TrainerService trainerService, TrainingTypeService trainingTypeService, TrainingService trainingService, UserService userService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingTypeService = trainingTypeService;
        this.trainingService = trainingService;
        this.userService = userService;
    }

    // 1. Add a new trainee
    @PostMapping(value = "/trainee")
    @ApiOperation(value = "Add a new trainee", response = ResponseEntity.class)
    public ResponseEntity<RegisterResponseDTO> registerTrainee(@ApiParam(value = "Trainee details", required = true) @RequestBody TraineeDTO traineeDTO) {
        Trainee trainee = traineeService.registerTrainee(traineeDTO);
        return new ResponseEntity<>(new RegisterResponseDTO(trainee.getUser().getUsername(), trainee.getUser().getPassword()), HttpStatus.CREATED);
    }

    // 2. Add a new trainer
    @PostMapping(value = "/trainer")
    @ApiOperation(value = "Add a new trainer", response = ResponseEntity.class)
    public ResponseEntity<RegisterResponseDTO> registerTrainer(@ApiParam(value = "Trainer details", required = true) @RequestBody TrainerDTO trainerDTO) {
        Trainer trainer = trainerService.registerTrainer(trainerDTO);
        return new ResponseEntity<>(new RegisterResponseDTO(trainer.getUser().getUsername(), trainer.getUser().getPassword()), HttpStatus.CREATED);
    }

    // 3. Login
    @GetMapping(value = "/user/login")
    @ApiOperation(value = "Login user", response = ResponseEntity.class)
    public ResponseEntity<User> loginUser(
            @ApiParam(value = "Username", required = true) @RequestParam String username,
            @ApiParam(value = "Password", required = true) @RequestParam String password) {
        return new ResponseEntity<>(userService.authenticateUser(username, password), HttpStatus.OK);
    }

    // 4. Update Password
    @PutMapping(value = "/user/{username}/password")
    @ApiOperation(value = "Update password for a user", response = ResponseEntity.class)
    public ResponseEntity<User> updatePassword(
            @ApiParam(value = "Username", required = true) @PathVariable String username,
            @ApiParam(value = "Old password", required = true) @RequestParam String oldPassword,
            @ApiParam(value = "New password", required = true) @RequestParam String newPassword) {
        return new ResponseEntity<>(userService.updatePassword(username, oldPassword, newPassword), HttpStatus.OK);
    }

    // 5. Get Trainee Profile by Username
    @GetMapping(value = "/trainees/{username}")
    @ApiOperation(value = "Get trainee profile by username", response = ResponseEntity.class)
    public ResponseEntity<TraineeDTO> getTraineeProfile(
            @ApiParam(value = "Username", required = true) @PathVariable String username) {
        Trainee trainee = traineeService.getTraineeByUsername(username);
        TraineeDTO traineeDTO = traineeService.convertTraineeToTraineeDTO(trainee);
        return new ResponseEntity<>(traineeDTO, HttpStatus.OK);
    }

    // 6. Update Trainee Profile
    @PutMapping(value = "/trainees/{username}")
    @ApiOperation(value = "Update trainee profile", response = ResponseEntity.class)
    public ResponseEntity<TraineeDTO> updateTraineeProfile(
            @ApiParam(value = "Username", required = true) @PathVariable String username,
            @ApiParam(value = "Password", required = true) @RequestParam String password,
            @ApiParam(value = "Trainee details", required = true) @RequestBody TraineeDTO traineeDTO) {
        Trainee trainee = traineeService.updateTrainee(traineeDTO, username, password);
        TraineeDTO traineeDTOResponse = traineeService.convertTraineeToTraineeDTO(trainee);
        return new ResponseEntity<>(traineeDTOResponse, HttpStatus.OK);
    }

    // 7. Delete Trainee Profile
    @DeleteMapping(value = "/trainees/{username}")
    @ApiOperation(value = "Delete trainee profile by username", response = ResponseEntity.class)
    public ResponseEntity<String> deleteTraineeProfile(
            @ApiParam(value = "Username", required = true) @PathVariable String username,
            @ApiParam(value = "Password", required = true) @RequestParam String password) {
        traineeService.deleteTrainee(username, password);
        return new ResponseEntity<>("Trainee profile deleted successfully", HttpStatus.OK);
    }

    // 8. Get Trainer Profile by Username
    @GetMapping(value = "/trainers/{username}")
    @ApiOperation(value = "Get trainer profile by username", response = ResponseEntity.class)
    public ResponseEntity<TrainerDTO> getTrainerProfile(
            @ApiParam(value = "Username", required = true) @PathVariable String username) {
        Trainer trainer = trainerService.getTrainerByUsername(username);
        TrainerDTO trainerDTO = trainerService.convertTrainerToTraineeDTO(trainer);
        return new ResponseEntity<>(trainerDTO, HttpStatus.OK);
    }

    // 9. Update Trainer Profile
    @PutMapping(value = "/trainers/{username}")
    @ApiOperation(value = "Update trainer profile", response = ResponseEntity.class)
    public ResponseEntity<TrainerDTO> updateTrainerProfile(
            @ApiParam(value = "Username", required = true) @PathVariable String username,
            @ApiParam(value = "Password", required = true) @RequestParam String password,
            @ApiParam(value = "Trainer details", required = true) @RequestBody TrainerDTO trainerDTO) {
        Trainer trainer = trainerService.updateTrainer(trainerDTO, username, password);
        TrainerDTO trainerDTOResponse = trainerService.convertTrainerToTraineeDTO(trainer);
        return new ResponseEntity<>(trainerDTOResponse, HttpStatus.OK);
    }

    // 10. Get not assigned on trainee active trainers
    @GetMapping(value = "/trainees/{username}/trainersNotAssigned")
    @ApiOperation(value = "Get trainers not assigned to the trainee", response = ResponseEntity.class)
    public ResponseEntity<List<Trainer>> getNotAssignedTrainers(
            @ApiParam(value = "Username", required = true) @PathVariable String username) {
        return new ResponseEntity<>(trainerService.getUnassignedTrainers(username), HttpStatus.OK);
    }

    // 11. update Trainee's Trainer List
    @PutMapping(value = "/trainees/{username}/trainers")
    @ApiOperation(value = "Update trainee's trainers list", response = ResponseEntity.class)
    public ResponseEntity<List<TrainerListItem>> updateTraineeTrainers(
            @ApiParam(value = "Username", required = true) @PathVariable String username,
            @ApiParam(value = "Password", required = true) @RequestParam String password,
            @ApiParam(value = "List of trainer usernames", required = true) @RequestBody List<String> trainerUsernames) {
        List<TrainerListItem> updatedTrainersList = traineeService.updateTrainersList(username, password, trainerUsernames);
        return new ResponseEntity<>(updatedTrainersList, HttpStatus.OK);
    }

    // 12. Get Trainee Trainings List
    @GetMapping(value = "/trainees/{username}/trainings")
    @ApiOperation(value = "Get trainee's trainings list", response = ResponseEntity.class)
    public ResponseEntity<List<TrainingResponse>> getTraineeTrainings(
            @ApiParam(value = "Username", required = true) @PathVariable String username,
            @ApiParam(value = "Training filter", required = true) @RequestBody TrainingFilteredDTO trainingFilteredDTO) {
        trainingFilteredDTO.setUsername(username);
        return new ResponseEntity<>(trainingService.getTraineeTrainingsFiltered(trainingFilteredDTO), HttpStatus.OK);
    }

    // 13. Get Trainer Trainings List
    @GetMapping(value = "/trainers/{username}/trainings")
    @ApiOperation(value = "Get trainer's trainings list", response = ResponseEntity.class)
    public ResponseEntity<List<TrainingResponse>> getTrainerTrainings(
            @ApiParam(value = "Username", required = true) @PathVariable String username,
            @ApiParam(value = "Training filter", required = true) @RequestBody TrainingFilteredDTO trainingFilteredDTO) {
        trainingFilteredDTO.setUsername(username);
        return new ResponseEntity<>(trainingService.getTrainerTrainingsFiltered(trainingFilteredDTO), HttpStatus.OK);
    }

    // 14. Add Training
    @PostMapping(value = "/training")
    @ApiOperation(value = "Add training", response = ResponseEntity.class)
    public ResponseEntity<TrainingDTO> addTraining(
            @ApiParam(value = "Training details", required = true) @RequestBody TrainingDTO trainingDTO) {
        return new ResponseEntity<>(trainingService.createTraining(trainingDTO), HttpStatus.CREATED);
    }

    // 15. Activate/Deactivate Trainee
    @PatchMapping(value = "/trainees/{username}/status")
    @ApiOperation(value = "Activate or deactivate trainee", response = ResponseEntity.class)
    public ResponseEntity<User> activateDeactivateTrainee(
            @ApiParam(value = "Username", required = true) @PathVariable String username,
            @ApiParam(value = "Password", required = true) @RequestParam String password,
            @ApiParam(value = "Active status", required = true) @RequestParam boolean isActive) {
        return new ResponseEntity<>(traineeService.ActivateDeactivateTrainee(username, password, isActive), HttpStatus.OK);
    }

    // 16. Activate/Deactivate Trainer
    @PatchMapping(value = "/trainers/{username}/status")
    @ApiOperation(value = "Activate or deactivate trainer", response = ResponseEntity.class)
    public ResponseEntity<User> activateDeactivateTrainer(
            @ApiParam(value = "Username", required = true) @PathVariable String username,
            @ApiParam(value = "Password", required = true) @RequestParam String password,
            @ApiParam(value = "Active status", required = true) @RequestParam boolean isActive) {
        return new ResponseEntity<>(trainerService.ActivateDeactivateTrainer(username, password, isActive), HttpStatus.OK);
    }

    // 17. Get Training Types
    @GetMapping(value = "/training-Types")
    @ApiOperation(value = "Get all training types", response = ResponseEntity.class)
    public ResponseEntity<List<TrainingType>> getAllTrainingTypes() {
        return new ResponseEntity<>(trainingTypeService.getAllTrainingTypes(), HttpStatus.OK);
    }
}
