package com.epam.taskgym.controller;

import com.epam.taskgym.models.TrainingRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "MICROSERVICE", fallback = MicroserviceFallback.class)
public interface MicroserviceClient {

    @PostMapping("/Workloads")
    void actionTraining(@RequestBody TrainingRequest trainingRequest);
}
