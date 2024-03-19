package com.epam.microservice.Controller;

import com.epam.microservice.model.TrainingRequest;
import com.epam.microservice.service.TrainingWorkService;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Workloads")
@RequiredArgsConstructor
public class TrainingWorkController {
    private final TrainingWorkService trainingWorkService;

    @PostMapping
    public ResponseEntity<String> actionTraining(@RequestBody TrainingRequest trainingRequest) {
        trainingWorkService.acceptTrainerWork(trainingRequest);
        return new ResponseEntity<>(trainingRequest.getAction() + "Action Completed successfully", HttpStatus.OK);
    }
}
