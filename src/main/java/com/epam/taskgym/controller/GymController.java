package com.epam.taskgym.controller;

import com.epam.taskgym.controller.response.RegisterResponse;
import com.epam.taskgym.entity.Trainee;
import com.epam.taskgym.entity.Trainer;
import com.epam.taskgym.entity.TrainingType;
import com.epam.taskgym.service.TraineeService;
import com.epam.taskgym.service.TrainerService;
import com.epam.taskgym.service.TrainingService;
import com.epam.taskgym.service.TrainingTypeService;
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

    @PostMapping(value = "/trainee", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RegisterResponse> registerTrainee(@RequestBody Map<String, String> traineeDetails) {
        Trainee trainee = traineeService.registerTrainee(traineeDetails);
        return new ResponseEntity<>(new RegisterResponse(trainee.getUser().getUsername(),trainee.getUser().getPassword()), HttpStatus.CREATED);
    }

    @PostMapping(value = "/trainer", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RegisterResponse> registerTrainer(@RequestBody Map<String, String> trainerDetails) {
        Trainer trainer = trainerService.registerTrainer(trainerDetails);
        return new ResponseEntity<>(new RegisterResponse(trainer.getUser().getUsername(),trainer.getUser().getPassword()), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/trainingtypes", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<TrainingType>> getAllTrainingTypes() {
        return new ResponseEntity<>(trainingTypeService.getAllTrainingTypes(), HttpStatus.OK);
    }
}
