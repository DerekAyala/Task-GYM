package com.epam.taskgym.controller;

import com.epam.taskgym.service.TrainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TrainerController {

    @Autowired
    private TrainerService trainerService;

    @GetMapping("/trainerLogin")
    public String showTrainerLoginForm() {
        return "trainerLogin";
    }

    @PostMapping("/trainerLogin")
    public String handleTrainerLogin(@RequestParam String username,
                                     @RequestParam String password,
                                     Model model) {
        boolean loginSuccessful = trainerService.authenticateTrainer(username, password);
        if (loginSuccessful) {
            return "redirect:/trainerDashboard";
        } else {
            model.addAttribute("errorMessage", "Invalid username or password. Please try again.");
            return "trainerLogin";
        }
    }

    @GetMapping("/trainerRegister")
    public String showTrainerRegistrationForm() {
        return "trainerRegister";
    }

    @PostMapping("/trainerRegister")
    public String handleTrainerRegistration(@RequestParam String firstName,
                                            @RequestParam String lastName,
                                            @RequestParam String specialization) {
        trainerService.registerTrainer(firstName, lastName, specialization);
        return "redirect:/trainerLogin";
    }
}
