package com.epam.taskgym.controller;

import com.epam.taskgym.models.TrainingRequest;
import org.springframework.stereotype.Component;

@Component
public class MicroserviceFallback implements MicroserviceClient {

    @Override
    public void actionTraining(TrainingRequest trainingRequest) {
        System.out.println("Microservice is not available");
    }
}
