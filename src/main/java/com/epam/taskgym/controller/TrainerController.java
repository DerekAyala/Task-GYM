package com.epam.taskgym.controller;

import com.epam.taskgym.dto.TrainerDTO;
import com.epam.taskgym.entity.Trainer;
import com.epam.taskgym.entity.User;
import com.epam.taskgym.service.TrainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@RestController
@RequestMapping("/api/trainers")
public class TrainerController {

    @Autowired
    private TrainerService trainerService;

    @PostMapping("/login")
    public String handleTrainerLogin(@RequestBody Map<String, String> body) {
        boolean loginSuccessful = trainerService.authenticateTrainer(body.get("username"), body.get("password"));
        if (loginSuccessful) {
            return "Login Successful";
        } else {
            return "Invalid username or password";
        }
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public TrainerDTO handleTrainerRegistration(@RequestBody Map<String, String> map) {
        return trainerService.registerTrainer(map.get("firstName"), map.get("lastName"), map.get("specialization"));
    }

    @GetMapping("/{username}")
    public TrainerDTO getTrainer(@PathVariable String username) {
        return trainerService.getTrainer(username);
    }

    @PutMapping("/{username}")
    public TrainerDTO updateTrainer(@PathVariable String username, @RequestBody Map<String, String> updates) {
        return trainerService.updateTrainer(username, updates);
    }

    @DeleteMapping("/{username}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTrainer(@PathVariable String username) {
        trainerService.deleteTrainer(username);
    }
}
