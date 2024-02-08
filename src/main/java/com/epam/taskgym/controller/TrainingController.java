package com.epam.taskgym.controller;

import com.epam.taskgym.entity.Training;
import com.epam.taskgym.service.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/trainings") // base url for all endpoints in this controller
public class TrainingController {

    private final TrainingService trainingService;

    @Autowired
    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @PostMapping
    public ResponseEntity<Training> createTraining(@RequestBody Map<String, String> trainingInfo) {
        /*
        Long traineeId = Long.parseLong(trainingInfo.get("traineeId"));
        Long trainerId = Long.parseLong(trainingInfo.get("trainerId"));
        String name = trainingInfo.get("name");
        Long trainingTypeId = Long.parseLong(trainingInfo.get("trainingTypeId"));
        Training training = trainingService.createTraining(traineeId, trainerId, name, trainingTypeId);

        if (training != null) {
            return new ResponseEntity<>(training, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

         */
        return ResponseEntity.ok(new Training());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Training> getTraining(@PathVariable Long id) {
        /*
        Training training = trainingService.getTraining(id);

        if (training != null) {
            return new ResponseEntity<>(training, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        */
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
