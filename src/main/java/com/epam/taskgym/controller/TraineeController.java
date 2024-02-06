package com.epam.taskgym.controller;

import com.epam.taskgym.dto.TraineeDTO;
import com.epam.taskgym.entity.User;
import com.epam.taskgym.service.TraineeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@RestController
@RequestMapping("/api/trainees")
public class TraineeController {

    @Autowired
    private TraineeService traineeService;

    @PostMapping("/login")
    public ResponseEntity<String> handleTraineeLogin(@RequestBody Map<String, String> body) {
        boolean loginSuccessful = traineeService.authenticateTrainee(body.get("username"), body.get("password"));
        if (loginSuccessful) {
            return ResponseEntity.ok("Trainee Login Successful");
        } else {
            return ResponseEntity.badRequest().body("Invalid username or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<TraineeDTO> handleTraineeRegistration(@RequestBody Map<String, String> map) {
        TraineeDTO result = traineeService.registerTrainee(map.get("firstName"), map.get("lastName"), map.get("dateOfBirth"), map.get("address"));
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @GetMapping("/{username}")
    public ResponseEntity<TraineeDTO> getTrainee(@PathVariable String username) {
        TraineeDTO result = traineeService.getTrainee(username);
        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{username}")
    public ResponseEntity<TraineeDTO> updateTrainee(@PathVariable String username, @RequestBody Map<String, String> updates) {
        try {
            TraineeDTO updatedTrainee = traineeService.updateTrainee(username, updates);
            return ResponseEntity.ok(updatedTrainee);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteTrainee(@PathVariable String username) {
        try {
            traineeService.deleteTrainee(username);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}