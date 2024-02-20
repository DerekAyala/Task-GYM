package com.epam.taskgym.controller;

import com.epam.taskgym.entity.TrainingType;
import com.epam.taskgym.service.TraineeService;
import com.epam.taskgym.service.TrainerService;
import com.epam.taskgym.service.TrainingService;
import com.epam.taskgym.service.TrainingTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/")
public class GymController {

    @Autowired
    private TraineeService traineeService;
    @Autowired
    private TrainerService trainerService;
    @Autowired
    private TrainingTypeService trainingTypeService;
    @Autowired
    private TrainingService trainingService;

    @RequestMapping(value = "/trainingtypes", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<TrainingType>> getAllTrainingTypes() {
        return new ResponseEntity<>(trainingTypeService.getAllTrainingTypes(), HttpStatus.OK);
    }
}
