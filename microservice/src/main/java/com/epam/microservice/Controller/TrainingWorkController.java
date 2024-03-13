package com.epam.microservice.Controller;

import com.epam.microservice.model.TrainingRequest;
import com.epam.microservice.service.TrainingWorkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TrainingWorkController {
    private final TrainingWorkService trainingWorkService;

    @PostMapping(value = "/training")
    public ResponseEntity<String> addTraining(@RequestBody TrainingRequest trainingRequest) {
        trainingWorkService.addTrainingWork(trainingRequest);
        return new ResponseEntity<>(trainingRequest.getAction() + "Action Completed successfully", HttpStatus.OK);
    }
}
