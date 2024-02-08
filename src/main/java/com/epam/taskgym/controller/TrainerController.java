package com.epam.taskgym.controller;

import com.epam.taskgym.dto.TrainerDTO;
import com.epam.taskgym.service.TrainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/trainers")
public class TrainerController {

    @Autowired
    private TrainerService trainerService;

    @PostMapping("/login")
    public ResponseEntity<String> handleTrainerLogin(@RequestBody Map<String, String> body) {
        /*
        boolean loginSuccessful = trainerService.authenticateTrainer(body.get("username"), body.get("password"));
        if (loginSuccessful) {
            return ResponseEntity.ok("Login Successful");
        } else {
            return ResponseEntity.badRequest().body("Invalid username or password");
        }
        */
        return new ResponseEntity<>("Login Successful", HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<TrainerDTO> handleTrainerRegistration(@RequestBody Map<String, String> map) {
        /*
        TrainerDTO result = trainerService.registerTrainer(map.get("firstName"), map.get("lastName"), map.get("specialization"));
        return new ResponseEntity<>(result, HttpStatus.CREATED);
        */
        return ResponseEntity.ok(new TrainerDTO());
    }

    @GetMapping("/{username}")
    public ResponseEntity<TrainerDTO> getTrainer(@PathVariable String username) {
        /*
        TrainerDTO result = trainerService.getTrainer(username);
        if(result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
         */
        return ResponseEntity.ok(new TrainerDTO());
    }

    @PutMapping("/{username}")
    public ResponseEntity<TrainerDTO> updateTrainer(@PathVariable String username, @RequestBody Map<String, String> updates) {
        /*
        try {
            TrainerDTO updatedTrainer = trainerService.updateTrainer(username, updates);
            return ResponseEntity.ok(updatedTrainer);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
        */
        return ResponseEntity.ok(new TrainerDTO());
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteTrainer(@PathVariable String username) {
        /*
        try {
            trainerService.deleteTrainer(username);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }

         */
        return ResponseEntity.noContent().build();
    }
}
