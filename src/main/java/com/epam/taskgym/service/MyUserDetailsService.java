package com.epam.taskgym.service;

import com.epam.taskgym.entity.MyUserPrincipal;
import com.epam.taskgym.entity.User;
import com.epam.taskgym.exception.NotFoundException;
import com.epam.taskgym.repository.TraineeRepository;
import com.epam.taskgym.repository.TrainerRepository;
import com.epam.taskgym.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    public MyUserDetailsService(UserRepository userRepository, TrainerRepository trainerRepository, TraineeRepository traineeRepository) {
        this.userRepository = userRepository;
        this.trainerRepository = trainerRepository;
        this.traineeRepository = traineeRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws NotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("User with username {" + username + "} not found"));
        String role = "";
        if (trainerRepository.findByUserUsername(username).isPresent()) {
            role = "ROLE_TRAINER";
        } else if (traineeRepository.findByUserUsername(username).isPresent()) {
            role = "ROLE_TRAINEE";
        }
        return new MyUserPrincipal(user, role);
    }
}
