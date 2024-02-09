package com.epam.taskgym.service;

import com.epam.taskgym.dto.TraineeDTO;
import com.epam.taskgym.entity.Trainee;
import com.epam.taskgym.entity.User;
import com.epam.taskgym.repository.TraineeRepository;
import com.epam.taskgym.repository.UserRepository;
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
        Optional<Trainee> trainee = traineeRepository.findByUserUsernameAndUserPassword(username, password);
        return trainee.isPresent();
    }

    public Optional<Trainee> getTraineeByUsername(String username) {
        return traineeRepository.findByUserUsername(username);
    }

    public Optional<Trainee> getTraineeById(Long id) {
        return traineeRepository.findById(id);
    }

    public TraineeDTO registerTrainee(Map<String, String> traineeDetails) {

        /*
        String username = generateUniqueUsername(firstName.toLowerCase(), lastName.toLowerCase());
        String password = generateRandomPassword();

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(password);
        user = userDAO.save(user);
        LOGGER.info("User Trainee saved with ID: {}", user.getId());

        Trainee trainee = new Trainee();
        trainee.setUserId(user.getId());
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);
        trainee = traineeDAO.save(trainee);
        LOGGER.info("Trainee saved with ID: {}", trainee.getId());

        TraineeDTO traineeDTO = new TraineeDTO();
        fillTraineeDTO(traineeDTO, user, trainee);
*/
        return new TraineeDTO();
    }

    public boolean updatePasssword(String username, String password, String newPassword) {
        if (authenticateTrainee(username, password)) {
            Optional<Trainee> traineeOptional = traineeRepository.findByUserUsername(username);
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
}
