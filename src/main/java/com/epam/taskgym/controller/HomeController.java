package com.epam.taskgym.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @RequestMapping("/")
    public String home() {
        return "welcome";
    }

    @GetMapping("/registrationSuccess")
    public String showRegistrationSuccess() {
        return "registrationSuccess";
    }
}
