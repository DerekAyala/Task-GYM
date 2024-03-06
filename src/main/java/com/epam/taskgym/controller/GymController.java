package com.epam.taskgym.controller;

import com.epam.taskgym.models.*;
import com.epam.taskgym.entity.Trainee;
import com.epam.taskgym.entity.Trainer;
import com.epam.taskgym.entity.TrainingType;
import com.epam.taskgym.entity.User;
import com.epam.taskgym.helpers.Builders;
import com.epam.taskgym.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GymController {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingTypeService trainingTypeService;
    private final TrainingService trainingService;
    private final UserService userService;

    // 1. Add a new trainee
    @PostMapping(value = "/trainee")
    public ResponseEntity<RegisterResponse> registerTrainee(@RequestBody TraineeDTO traineeDTO) {
        return new ResponseEntity<>(traineeService.registerTrainee(traineeDTO), HttpStatus.CREATED);
    }

    // 2. Add a new trainer
    @PostMapping(value = "/trainer")
    public ResponseEntity<RegisterResponse> registerTrainer(@RequestBody TrainerDTO trainerDTO) {
        return new ResponseEntity<>(trainerService.registerTrainer(trainerDTO), HttpStatus.CREATED);
    }

    // 3. Login
    @GetMapping(value = "/user/login")
    public ResponseEntity<User> loginUser(
            @RequestParam String username,
            @RequestParam String password) {
        return new ResponseEntity<>(userService.authenticateUser(username, password), HttpStatus.OK);
    }

    // 4. Update Password
    @PutMapping(value = "/user/{username}/password")
    public ResponseEntity<User> updatePassword(
            @PathVariable String username,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        return new ResponseEntity<>(userService.updatePassword(username, oldPassword, newPassword), HttpStatus.OK);
    }

    // 5. Get Trainee Profile by Username
    @GetMapping(value = "/trainees/{username}")
    public ResponseEntity<TraineeDTO> getTraineeProfile(
            @PathVariable String username) {
        Trainee trainee = traineeService.getTraineeByUsername(username);
        TraineeDTO traineeDTO = Builders.convertTraineeToTraineeDTO(trainee);
        return new ResponseEntity<>(traineeDTO, HttpStatus.OK);
    }

    // 6. Update Trainee Profile
    @PutMapping(value = "/trainees/{username}")
    public ResponseEntity<TraineeDTO> updateTraineeProfile(
            @PathVariable String username,
            @RequestBody TraineeDTO traineeDTO) {
        Trainee trainee = traineeService.updateTrainee(traineeDTO, username);
        TraineeDTO traineeDTOResponse = Builders.convertTraineeToTraineeDTO(trainee);
        return new ResponseEntity<>(traineeDTOResponse, HttpStatus.OK);
    }

    // 7. Delete Trainee Profile
    @DeleteMapping(value = "/trainees/{username}")
    public ResponseEntity<String> deleteTraineeProfile(
            @PathVariable String username) {
        traineeService.deleteTrainee(username);
        return new ResponseEntity<>("Trainee profile deleted successfully", HttpStatus.OK);
    }

    // 8. Get Trainer Profile by Username
    @GetMapping(value = "/trainers/{username}")
    public ResponseEntity<TrainerDTO> getTrainerProfile(
            @PathVariable String username) {
        Trainer trainer = trainerService.getTrainerByUsername(username);
        TrainerDTO trainerDTO = Builders.convertTrainerToTraineeDTO(trainer);
        return new ResponseEntity<>(trainerDTO, HttpStatus.OK);
    }

    // 9. Update Trainer Profile
    @PutMapping(value = "/trainers/{username}")
    public ResponseEntity<TrainerDTO> updateTrainerProfile(
            @PathVariable String username,
            @RequestBody TrainerDTO trainerDTO) {
        Trainer trainer = trainerService.updateTrainer(trainerDTO, username);
        TrainerDTO trainerDTOResponse = Builders.convertTrainerToTraineeDTO(trainer);
        return new ResponseEntity<>(trainerDTOResponse, HttpStatus.OK);
    }

    // 10. Get not assigned on trainee active trainers
    @GetMapping(value = "/trainees/{username}/trainersNotAssigned")
    public ResponseEntity<List<TrainerListItem>> getNotAssignedTrainers(
            @PathVariable String username) {
        return new ResponseEntity<>(trainerService.getUnassignedTrainers(username), HttpStatus.OK);
    }

    // 11. update Trainee's Trainer List
    @PutMapping(value = "/trainees/{username}/trainers")
    public ResponseEntity<List<TrainerListItem>> updateTraineeTrainers(
            @PathVariable String username,
            @RequestBody List<String> trainerUsernames) {
        List<TrainerListItem> updatedTrainersList = traineeService.updateTrainersList(username, trainerUsernames);
        return new ResponseEntity<>(updatedTrainersList, HttpStatus.OK);
    }

    // 12. Get Trainee Trainings List
    @GetMapping(value = "/trainees/{username}/trainings")
    public ResponseEntity<List<TrainingResponse>> getTraineeTrainings(
            @PathVariable String username,
            @RequestBody TrainingFilteredDTO trainingFilteredDTO) {
        trainingFilteredDTO.setUsername(username);
        return new ResponseEntity<>(trainingService.getTraineeTrainingsFiltered(trainingFilteredDTO), HttpStatus.OK);
    }

    // 13. Get Trainer Trainings List
    @GetMapping(value = "/trainers/{username}/trainings")
    public ResponseEntity<List<TrainingResponse>> getTrainerTrainings(
            @PathVariable String username,
            @RequestBody TrainingFilteredDTO trainingFilteredDTO) {
        trainingFilteredDTO.setUsername(username);
        return new ResponseEntity<>(trainingService.getTrainerTrainingsFiltered(trainingFilteredDTO), HttpStatus.OK);
    }

    // 14. Add Training
    @PostMapping(value = "/training")
    public ResponseEntity<TrainingDTO> addTraining(
            @RequestBody TrainingDTO trainingDTO) {
        return new ResponseEntity<>(trainingService.createTraining(trainingDTO), HttpStatus.CREATED);
    }

    // 15. Activate/Deactivate Trainee
    @PatchMapping(value = "/trainees/{username}/status")
    public ResponseEntity<TraineeDTO> activateDeactivateTrainee(
            @PathVariable String username,
            @RequestParam boolean isActive) {
        return new ResponseEntity<>(traineeService.ActivateDeactivateTrainee(username, isActive), HttpStatus.OK);
    }

    // 16. Activate/Deactivate Trainer
    @PatchMapping(value = "/trainers/{username}/status")
    public ResponseEntity<TrainerDTO> activateDeactivateTrainer(
            @PathVariable String username,
            @RequestParam boolean isActive) {
        return new ResponseEntity<>(trainerService.ActivateDeactivateTrainer(username, isActive), HttpStatus.OK);
    }

    // 17. Get Training Types
    @GetMapping(value = "/training-Types")
    public ResponseEntity<List<TrainingType>> getAllTrainingTypes() {
        return new ResponseEntity<>(trainingTypeService.getAllTrainingTypes(), HttpStatus.OK);
    }
}
