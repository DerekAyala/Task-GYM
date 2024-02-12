package com.epam.taskgym.service;

import com.epam.taskgym.dto.TraineeDTO;
import com.epam.taskgym.entity.Trainee;
import com.epam.taskgym.entity.User;
import com.epam.taskgym.repository.TraineeRepository;
import com.epam.taskgym.service.exception.BadRequestException;
import com.epam.taskgym.service.exception.FailAuthenticateException;
import com.epam.taskgym.service.exception.MissingAttributes;
import com.epam.taskgym.service.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
public class TraineeService {

    @Autowired
    private TraineeRepository traineeRepository;
    @Autowired
    private UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeService.class);

    public void authenticateTrainee(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new MissingAttributes("Username and password are required");
        }
        if (traineeRepository.findByUserUsernameAndUserPassword(username, password).isEmpty()) {
            throw new FailAuthenticateException("Fail to authenticate");
        }
    }

    public Trainee getTraineeByUsername(String username) {
        Optional<Trainee> trainee = traineeRepository.findByUserUsername(username);
        if (trainee.isEmpty()) {
            throw new NotFoundException("Trainee with username {" + username + "} not found");
        }
        return trainee.get();
    }

    public TraineeDTO registerTrainee(Map<String, String> traineeDetails) {
        if (traineeDetails == null || traineeDetails.isEmpty()) {
            throw new MissingAttributes("Trainee details cannot be null or empty");
        }
        User user = userService.createUser(traineeDetails);

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        addDate(traineeDetails, trainee);
        trainee.setAddress(traineeDetails.getOrDefault("address", ""));
        traineeRepository.save(trainee);
        return fillTrainerDTO(user, trainee);
    }

    private void addDate(Map<String, String> traineeDetails, Trainee trainee) {
        if ((traineeDetails.containsKey("dateOfBirth") || !traineeDetails.get("dateOfBirth").isEmpty())) {
            Date dateOfBirth = userService.validateDate(traineeDetails.get("dateOfBirth"));
            trainee.setDateOfBirth(dateOfBirth);
        }
    }

    public TraineeDTO updateTrainee(Map<String, String> traineeDetails, String username, String password) {
        if (traineeDetails == null || traineeDetails.isEmpty()) {
            throw new MissingAttributes("Trainee Update details cannot be null or empty");
        }
        authenticateTrainee(username, password);
        Trainee trainee = getTraineeByUsername(username);
        User user = userService.updateUser(traineeDetails, trainee.getUser());
        trainee.setUser(user);
        addDate(traineeDetails, trainee);
        trainee.setAddress(traineeDetails.getOrDefault("address", trainee.getAddress()));
        traineeRepository.save(trainee);
        return fillTrainerDTO(user, trainee);
    }

    public boolean deleteTrainee(String username, String password) {
        authenticateTrainee(username, password);
        Trainee trainee = getTraineeByUsername(username);
        userService.deleteUser(trainee.getUser());
        traineeRepository.delete(trainee);
        return true;
    }

    public boolean updatePasssword(String username, String password, String newPassword) {
        authenticateTrainee(username, password);
        Trainee trainee = getTraineeByUsername(username);
        User user = trainee.getUser();
        user.setPassword(newPassword);
        userService.saveUser(user);
        trainee.setUser(user);
        traineeRepository.save(trainee);
        return true;
    }

    private TraineeDTO fillTrainerDTO(User user, Trainee trainee) {
        return new TraineeDTO(user, user.getUsername(), user.getPassword(), user.getFirstName(), user.getLastName(), trainee, trainee.getDateOfBirth(), trainee.getAddress());
    }
}
