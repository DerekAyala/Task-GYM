package com.epam.taskgym.service;

import com.epam.taskgym.dto.TraineeDTO;
import com.epam.taskgym.entity.Trainee;
import com.epam.taskgym.entity.User;
import com.epam.taskgym.repository.TraineeRepository;
import com.epam.taskgym.repository.UserRepository;
import com.epam.taskgym.service.exception.FailAuthenticateException;
import com.epam.taskgym.service.exception.MissingAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

@Service
public class TraineeService {


    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeService.class);


    public boolean authenticateTrainee(String username, String password) {
        return traineeRepository.findByUserUsernameAndUserPassword(username, password).isPresent();
    }

    public Optional<Trainee> getTraineeByUsername(String username) {
        return traineeRepository.findByUserUsername(username);
    }

    public TraineeDTO registerTrainee(Map<String, String> traineeDetails) {
        if (traineeDetails == null || traineeDetails.isEmpty()) {
            throw new MissingAttributes("Trainee details cannot be null or empty");
        }
        User user = userService.createUser(traineeDetails);

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        trainee.setDateOfBirth(traineeDetails.getOrDefault("dateOfBirth", ""));
        trainee.setAddress(traineeDetails.getOrDefault("address", ""));
        traineeRepository.save(trainee);
        return fillTrainerDTO(user, trainee);
    }

    public TraineeDTO updateTrainee(Map<String, String> traineeDetails, String username, String password) {
        if (traineeDetails == null || traineeDetails.isEmpty()) {
            throw new MissingAttributes("Trainee Update details cannot be null or empty");
        }
        if (authenticateTrainee(username, password)) {
            Optional<Trainee> traineeOptional = traineeRepository.findByUserUsername(username);
            if (traineeOptional.isPresent()) {
                Trainee trainee = traineeOptional.get();
                User user = userService.updateUser(traineeDetails, trainee.getUser());
                trainee.setUser(user);
                trainee.setDateOfBirth(traineeDetails.getOrDefault("dateOfBirth", trainee.getDateOfBirth()));
                trainee.setAddress(traineeDetails.getOrDefault("address", trainee.getAddress()));
                traineeRepository.save(trainee);
                return fillTrainerDTO(user, trainee);
            }
        } else {
            throw new FailAuthenticateException("Fail to authenticate");
        }
        return null;
    }

    public boolean updatePasssword(String username, String password, String newPassword) {
        if (authenticateTrainee(username, password)) {
            Optional<Trainee> traineeOptional = getTraineeByUsername(username);
            if (traineeOptional.isPresent()) {
                Trainee trainee = traineeOptional.get();
                User user = trainee.getUser();
                user.setPassword(newPassword);
                userRepository.save(user);
                trainee.setUser(user);
                traineeRepository.save(trainee);
                return true;
            }
        }
        return false;
    }

    private TraineeDTO fillTrainerDTO(User user,Trainee trainee) {
        return new TraineeDTO(user, user.getUsername(), user.getPassword(), user.getFirstName(), user.getLastName(), trainee, trainee.getDateOfBirth(), trainee.getAddress());
    }
}
