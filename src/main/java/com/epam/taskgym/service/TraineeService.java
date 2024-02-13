package com.epam.taskgym.service;

import com.epam.taskgym.dto.TraineeDTO;
import com.epam.taskgym.entity.Trainee;
import com.epam.taskgym.entity.User;
import com.epam.taskgym.repository.TraineeRepository;
import com.epam.taskgym.repository.TrainingRepository;
import com.epam.taskgym.service.exception.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
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
    @Autowired
    private TrainingRepository trainingRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeService.class);

    private void authenticateTrainee(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new MissingAttributes("Username and password are required");
        }
        Trainee trainee = getTraineeByUsername(username);
        if (!trainee.getUser().getPassword().equals(password)) {
            throw new FailAuthenticateException("Fail to authenticate: Password and username do not match");
        }
    }

    public Trainee getTraineeByUsername(String username) {
        Optional<Trainee> trainee = traineeRepository.findByUserUsername(username);
        if (trainee.isEmpty()) {
            throw new NotFoundException("Trainee with username {" + username + "} not found");
        }
        return trainee.get();
    }

    @Transactional
    public TraineeDTO registerTrainee(Map<String, String> traineeDetails) {
        validateTraineeDetails(traineeDetails);
        User user = userService.createUser(traineeDetails);

        Trainee trainee = new Trainee();
        trainee.setUser(user);
        addDate(traineeDetails, trainee);
        trainee.setAddress(traineeDetails.getOrDefault("address", ""));
        traineeRepository.save(trainee);
        return fillTrainerDTO(user, trainee);
    }

    @Transactional
    public TraineeDTO updateTrainee(Map<String, String> traineeDetails, String username, String password) {
        authenticateTrainee(username, password);
        validateTraineeDetails(traineeDetails);
        Trainee trainee = getTraineeByUsername(username);
        User user = userService.updateUser(traineeDetails, trainee.getUser());
        trainee.setUser(user);
        addDate(traineeDetails, trainee);
        trainee.setAddress(traineeDetails.getOrDefault("address", ""));
        traineeRepository.save(trainee);
        return fillTrainerDTO(user, trainee);
    }

    @Transactional
    public void deleteTrainee(String username, String password) {
        authenticateTrainee(username, password);
        Trainee trainee = getTraineeByUsername(username);

        try {
            trainingRepository.deleteAllByTrainee_User_Username(username);
            User user = trainee.getUser();
            traineeRepository.delete(trainee);
            userService.deleteUser(user);
        } catch (DataAccessException e) {
            throw new TraineeDeletionException("An error occurred while deleting trainee with username: " + username + " " + e.getMessage(), e);
        }
    }

    @Transactional
    public void updatePasssword(String username, String password, String newPassword) {
        authenticateTrainee(username, password);
        validatePassword(newPassword);
        Trainee trainee = getTraineeByUsername(username);
        User user = trainee.getUser();
        user.setPassword(newPassword);
        userService.saveUser(user);
        trainee.setUser(user);
        traineeRepository.save(trainee);
    }

    public Date validateDate(String StringDate) {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        Date date = null;
        try {
            date = df.parse(StringDate);
        } catch (ParseException e) {
            throw new BadRequestException("Invalid date format {DD-MM-YYYY}");
        }
        return date;
    }

    private TraineeDTO fillTrainerDTO(User user, Trainee trainee) {
        return new TraineeDTO(user, user.getUsername(), user.getPassword(), user.getFirstName(), user.getLastName(), trainee, trainee.getDateOfBirth(), trainee.getAddress());
    }

    private void addDate(Map<String, String> traineeDetails, Trainee trainee) {
        if ((traineeDetails.containsKey("dateOfBirth") || !traineeDetails.get("dateOfBirth").isEmpty())) {
            Date dateOfBirth = validateDate(traineeDetails.get("dateOfBirth"));
            trainee.setDateOfBirth(dateOfBirth);
        }
    }

    private void validateTraineeDetails(Map<String, String> traineeDetails) {
        if (traineeDetails == null || traineeDetails.isEmpty()) {
            throw new MissingAttributes("Trainee details cannot be null or empty");
        }
    }

    public static void validatePassword(String newPassword) {
        if(newPassword == null || newPassword.isEmpty()){
            throw new InvalidPasswordException("Password cannot be null or empty.");
        }

        if(newPassword.length() < 8){
            throw new InvalidPasswordException("Password must be at least 8 characters long.");
        }
    }
}
