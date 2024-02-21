package com.epam.taskgym.controller;

import com.epam.taskgym.controller.helpers.RegisterResponse;
import com.epam.taskgym.controller.helpers.TraineeDetails;
import com.epam.taskgym.controller.helpers.TrainerDetails;
import com.epam.taskgym.controller.helpers.TrainingDetails;
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
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RegisterResponse> registerTrainee(@RequestBody TraineeDetails traineeDetails) {
        Trainee trainee = traineeService.registerTrainee(traineeDetails);
        return new ResponseEntity<>(new RegisterResponse(trainee.getUser().getUsername(),trainee.getUser().getPassword()), HttpStatus.CREATED);
    }

    // 2. Add a new trainer
    @PostMapping(value = "/trainer")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RegisterResponse> registerTrainer(@RequestBody TrainerDetails trainerDetails) {
        Trainer trainer = trainerService.registerTrainer(trainerDetails);
        return new ResponseEntity<>(new RegisterResponse(trainer.getUser().getUsername(),trainer.getUser().getPassword()), HttpStatus.CREATED);
    }

    // 3. Login
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<User> loginUser(@RequestParam String username, @RequestParam String password) {
        return new ResponseEntity<>(userService.authenticateUser(username, password), HttpStatus.OK);
    }

    // 4. Update Password
    @RequestMapping(value = "/password", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<User> updatePassword(@RequestParam String username, @RequestParam String oldPassword, @RequestParam String newPassword) {
        return new ResponseEntity<>(userService.updatePassword(username, oldPassword, newPassword), HttpStatus.OK);
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
    public ResponseEntity<Trainee> updateTraineeProfile(@PathVariable String username, @RequestParam String password, @RequestBody TraineeDetails traineeDetails) {
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

    // 9. Update Trainer Profile
    @RequestMapping(value = "/trainer/{username}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Trainer> updateTrainerProfile(@PathVariable String username, @RequestParam String password, @RequestBody TrainerDetails trainerDetails) {
        return new ResponseEntity<>(trainerService.updateTrainer(trainerDetails, username, password), HttpStatus.OK);
    }

    // 14. Add Training
    @RequestMapping(value = "/training", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> addTraining(@RequestBody TrainingDetails trainingDetails) {
        trainingService.createTraining(trainingDetails);
        return new ResponseEntity<>("Training added successfully", HttpStatus.CREATED);
    }

    // 17. Get Training Types
    @RequestMapping(value = "/trainingtypes", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<TrainingType>> getAllTrainingTypes() {
        return new ResponseEntity<>(trainingTypeService.getAllTrainingTypes(), HttpStatus.OK);
    }
}
