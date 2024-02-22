package com.epam.taskgym.controller;

import com.epam.taskgym.dto.RegisterResponseDTO;
import com.epam.taskgym.dto.TraineeDTO;
import com.epam.taskgym.dto.TrainerDTO;
import com.epam.taskgym.dto.TrainingDTO;
import com.epam.taskgym.entity.Trainee;
import com.epam.taskgym.entity.Trainer;
import com.epam.taskgym.entity.TrainingType;
import com.epam.taskgym.entity.User;
import com.epam.taskgym.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
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
    public ResponseEntity<RegisterResponseDTO> registerTrainee(@RequestBody TraineeDTO traineeDTO) {
        Trainee trainee = traineeService.registerTrainee(traineeDTO);
        return new ResponseEntity<>(new RegisterResponseDTO(trainee.getUser().getUsername(),trainee.getUser().getPassword()), HttpStatus.CREATED);
    }

    // 2. Add a new trainer
    @PostMapping(value = "/trainer")
    public ResponseEntity<RegisterResponseDTO> registerTrainer(@RequestBody TrainerDTO trainerDTO) {
        Trainer trainer = trainerService.registerTrainer(trainerDTO);
        return new ResponseEntity<>(new RegisterResponseDTO(trainer.getUser().getUsername(),trainer.getUser().getPassword()), HttpStatus.CREATED);
    }

    // 3. Login
    @GetMapping(value = "/user/login")
    public ResponseEntity<User> loginUser(@RequestParam String username, @RequestParam String password) {
        return new ResponseEntity<>(userService.authenticateUser(username, password), HttpStatus.OK);
    }

    // 4. Update Password
    @PutMapping(value = "/user/{username}/password")
    public ResponseEntity<User> updatePassword(@PathVariable String username, @RequestParam String oldPassword, @RequestParam String newPassword) {
        return new ResponseEntity<>(userService.updatePassword(username, oldPassword, newPassword), HttpStatus.OK);
    }

    // 5. Get Trainee Profile by Username
    @GetMapping(value = "/trainees/{username}")
    public ResponseEntity<Trainee> getTraineeProfile(@PathVariable String username) {
        return new ResponseEntity<>(traineeService.getTraineeByUsername(username), HttpStatus.OK);
    }

    // 6. Update Trainee Profile
    @PutMapping(value = "/trainees/{username}")
    public ResponseEntity<Trainee> updateTraineeProfile(@PathVariable String username, @RequestParam String password, @RequestBody TraineeDTO traineeDTO) {
        return new ResponseEntity<>(traineeService.updateTrainee(traineeDTO, username, password), HttpStatus.OK);
    }

    // 7. Delete Trainee Profile
    @DeleteMapping(value = "/trainees/{username}")
    public ResponseEntity<String> deleteTraineeProfile(@PathVariable String username, @RequestParam String password) {
        traineeService.deleteTrainee(username, password);
        return new ResponseEntity<>("Trainee profile deleted successfully", HttpStatus.OK);
    }

    // 8. Get Trainer Profile by Username
    @GetMapping(value = "/trainers/{username}")
    public ResponseEntity<Trainer> getTrainerProfile(@PathVariable String username) {
        return new ResponseEntity<>(trainerService.getTrainerByUsername(username), HttpStatus.OK);
    }

    // 9. Update Trainer Profile
    @PutMapping(value = "/trainers/{username}")
    public ResponseEntity<Trainer> updateTrainerProfile(@PathVariable String username, @RequestParam String password, @RequestBody TrainerDTO trainerDTO) {
        return new ResponseEntity<>(trainerService.updateTrainer(trainerDTO, username, password), HttpStatus.OK);
    }

    // 10. Get not assigned on trainee active trainers
    @GetMapping(value = "/trainees/{username}/trainersNotAssigned")
    public ResponseEntity<List<Trainer>> getNotAssignedTrainers(@PathVariable String username) {
        return new ResponseEntity<>(trainerService.getUnassignedTrainers(username), HttpStatus.OK);
    }

    // 14. Add Training
    @PostMapping(value = "/training")
    public ResponseEntity<TrainingDTO> addTraining(@RequestBody TrainingDTO trainingDTO) {
        return new ResponseEntity<>(trainingService.createTraining(trainingDTO), HttpStatus.CREATED);
    }

    // 15. Activate/Deactivate Trainee
    @PatchMapping(value = "/trainees/{username}/status")
    public ResponseEntity<User> activateDeactivateTrainee(@PathVariable String username, @RequestParam String password, @RequestParam boolean isActive) {
        return new ResponseEntity<>(traineeService.ActivateDeactivateTrainee(username, password, isActive), HttpStatus.OK);
    }

    // 16. Activate/Deactivate Trainer
    @PatchMapping(value = "/trainers/{username}/status")
    public ResponseEntity<User> activateDeactivateTrainer(@PathVariable String username, @RequestParam String password, @RequestParam boolean isActive) {
        return new ResponseEntity<>(trainerService.ActivateDeactivateTrainer(username, password, isActive), HttpStatus.OK);
    }

    // 17. Get Training Types
    @GetMapping(value = "/training-Types")
    public ResponseEntity<List<TrainingType>> getAllTrainingTypes() {
        return new ResponseEntity<>(trainingTypeService.getAllTrainingTypes(), HttpStatus.OK);
    }
}
