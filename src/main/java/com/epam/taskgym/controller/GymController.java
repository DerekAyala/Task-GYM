package com.epam.taskgym.controller;

import com.epam.taskgym.controller.response.RegisterResponse;
import com.epam.taskgym.entity.Trainee;
import com.epam.taskgym.entity.Trainer;
import com.epam.taskgym.entity.TrainingType;
import com.epam.taskgym.entity.User;
import com.epam.taskgym.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class GymController {

    @Autowired
    private TraineeService traineeService;
    @Autowired
    private TrainerService trainerService;
    @Autowired
    private TrainingTypeService trainingTypeService;
    @Autowired
    private TrainingService trainingService;
    @Autowired
    private UserService userService;

    // 1. Add a new trainee
    @PostMapping(value = "/trainee", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RegisterResponse> registerTrainee(@RequestBody Map<String, String> traineeDetails) {
        Trainee trainee = traineeService.registerTrainee(traineeDetails);
        return new ResponseEntity<>(new RegisterResponse(trainee.getUser().getUsername(),trainee.getUser().getPassword()), HttpStatus.CREATED);
    }

    // 2. Add a new trainer
    @PostMapping(value = "/trainer", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RegisterResponse> registerTrainer(@RequestBody Map<String, String> trainerDetails) {
        Trainer trainer = trainerService.registerTrainer(trainerDetails);
        return new ResponseEntity<>(new RegisterResponse(trainer.getUser().getUsername(),trainer.getUser().getPassword()), HttpStatus.CREATED);
    }

    // 3. Login
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<User> loginUser(@RequestParam String username, @RequestParam String password) {
        return new ResponseEntity<>(userService.authenticateUser(username, password), HttpStatus.OK);
    }

    // 5. Get Trainee Profile by Username
    @RequestMapping(value = "/trainee/{username}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Trainee> getTraineeProfile(@PathVariable String username) {
        return new ResponseEntity<>(traineeService.getTraineeByUsername(username), HttpStatus.OK);
    }

    // 6. Update Trainee Profile
    @RequestMapping(value = "/trainee/{username}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Trainee> updateTraineeProfile(@PathVariable String username, @RequestParam String password, @RequestBody Map<String, String> traineeDetails) {
        return new ResponseEntity<>(traineeService.updateTrainee(traineeDetails, username, password), HttpStatus.OK);
    }

    // 7. Delete Trainee Profile
    @RequestMapping(value = "/trainee/{username}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteTraineeProfile(@PathVariable String username, @RequestParam String password) {
        traineeService.deleteTrainee(username, password);
        return new ResponseEntity<>("Trainee profile deleted successfully", HttpStatus.OK);
    }

    // 8. Get Trainer Profile by Username
    @RequestMapping(value = "/trainer/{username}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Trainer> getTrainerProfile(@PathVariable String username) {
        return new ResponseEntity<>(trainerService.getTrainerByUsername(username), HttpStatus.OK);
    }

    // 17. Get Training Types
    @RequestMapping(value = "/trainingtypes", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<TrainingType>> getAllTrainingTypes() {
        return new ResponseEntity<>(trainingTypeService.getAllTrainingTypes(), HttpStatus.OK);
    }
}
