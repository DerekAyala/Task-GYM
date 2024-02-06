package com.epam.taskgym.controller;

import com.epam.taskgym.service.TraineeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TraineeController {

    @Autowired
    private TraineeService traineeService;

    @GetMapping("/traineeLogin")
    public String showTraineeLoginForm() {
        return "traineeLogin";
    }

    @PostMapping("/traineeLogin")
    public String handleTraineeLogin(@RequestParam String username,
                                     @RequestParam String password,
                                     Model model) {
        boolean loginSuccessful = traineeService.authenticateTrainee(username, password);
        if (loginSuccessful) {
            return "redirect:/traineeDashboard"; // Redirect to the trainee's dashboard after successful login
        } else {
            model.addAttribute("errorMessage", "Invalid username or password. Please try again.");
            return "traineeLogin";
        }
    }

    @GetMapping("/traineeRegister")
    public String showTraineeRegistrationForm() {
        return "traineeRegister";
    }

    @PostMapping("/traineeRegister")
    public String handleTraineeRegistration(@RequestParam String firstName,
                                            @RequestParam String lastName,
                                            @RequestParam String dateOfBirth,
                                            @RequestParam String address) {
        traineeService.registerTrainee(firstName, lastName, dateOfBirth, address);
        return "redirect:/traineeLogin"; // Redirect to login after successful registration
    }
    // Other handlers
}