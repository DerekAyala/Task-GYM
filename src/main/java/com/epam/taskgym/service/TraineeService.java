package com.epam.taskgym.service;

import com.epam.taskgym.dao.TraineeDAO;
import com.epam.taskgym.dao.UserDAO;
import com.epam.taskgym.dto.TraineeDTO;
import com.epam.taskgym.entity.Trainee;
import com.epam.taskgym.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class TraineeService {

    private final UserDAO userDAO;
    private final TraineeDAO traineeDAO;
    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeService.class);
    @Autowired
    public TraineeService(UserDAO userDAO, TraineeDAO traineeDAO) {
        this.userDAO = userDAO;
        this.traineeDAO = traineeDAO;
    }

    public boolean authenticateTrainee(String username, String password) {
        Trainee trainee = traineeDAO.findByUsernameAndPassword(username, password);
        return trainee != null;
    }

    public TraineeDTO registerTrainee(String firstName, String lastName, String dateOfBirth, String address) {
        String username = generateUniqueUsername(firstName.toLowerCase(), lastName.toLowerCase());
        String password = generateRandomPassword();

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(password);
        user = userDAO.save(user);
        LOGGER.info("User saved with ID: {}", user.getId());

        Trainee trainee = new Trainee();
        trainee.setUserId(user.getId());
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);
        trainee = traineeDAO.save(trainee);
        LOGGER.info("Trainee saved with ID: {}", trainee.getId());

        TraineeDTO traineeDTO = new TraineeDTO();
        fillTraineeDTO(traineeDTO, user, trainee);

        return traineeDTO;
    }

    public TraineeDTO getTrainee(String username) {
        Trainee trainee = traineeDAO.findByUsername(username);
        if (trainee != null) {
            Optional<User> userOptional = userDAO.findById(trainee.getUserId());
            if (userOptional.isPresent()) {
                LOGGER.info("Trainee was found");
                User user = userOptional.get();

                TraineeDTO traineeDTO = new TraineeDTO();
                fillTraineeDTO(traineeDTO, user, trainee);
                return traineeDTO;
            }
        }
        return null;
    }

    public TraineeDTO updateTrainee(String username, Map<String, String> updates) {
        User user = userDAO.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        LOGGER.info("User was found");
        Trainee trainee = traineeDAO.findByUserId(user.getId());
        if (trainee == null) {
            throw new RuntimeException("Trainee not found");
        }
        LOGGER.info("Trainee was found");

        if (updates.containsKey("firstName")) {
            LOGGER.info("updates contains firstName");
            String firstName = updates.get("firstName");
            user.setFirstName(firstName);
        }

        if (updates.containsKey("lastName")) {
            LOGGER.info("updates contains lastName");
            String lastName = updates.get("lastName");
            user.setLastName(lastName);
        }

        userDAO.update(user);

        if (updates.containsKey("dateOfBirth")) {
            LOGGER.info("updates contains dateOfBirth");
            String dob = updates.get("dateOfBirth");
            trainee.setDateOfBirth(dob);
        }

        if (updates.containsKey("address")) {
            LOGGER.info("updates contains address");
            String addr = updates.get("address");
            trainee.setAddress(addr);
        }

        traineeDAO.update(trainee);

        TraineeDTO traineeDTO = new TraineeDTO();
        fillTraineeDTO(traineeDTO, user, trainee);

        return traineeDTO;
    }

    public void deleteTrainee(String username) {
        Optional<User> user = userDAO.findByUsername(username);
        if (user.isPresent()) {
            LOGGER.info("User was found");
            Trainee trainee = traineeDAO.findByUserId(user.get().getId());
            if (trainee != null) {
                LOGGER.info("Trainee was found");
                traineeDAO.deleteById(trainee.getId());
                LOGGER.info("Trainee was deleted");
                userDAO.deleteById(user.get().getId());
                LOGGER.info("User was deleted");
            } else {
                throw new RuntimeException("Trainee not found");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }

    private void fillTraineeDTO(TraineeDTO traineeDTO, User user, Trainee trainee) {
        traineeDTO.setUserId(user.getId());
        traineeDTO.setUsername(user.getUsername());
        traineeDTO.setPassword(user.getPassword());
        traineeDTO.setFirstName(user.getFirstName());
        traineeDTO.setLastName(user.getLastName());
        traineeDTO.setTraineeId(trainee.getId());
        traineeDTO.setDateOfBirth(trainee.getDateOfBirth());
        traineeDTO.setAddress(trainee.getAddress());
    }

    public String generateUniqueUsername(String firstName, String lastName) {
        String baseUsername = firstName + "." + lastName;
        String username = baseUsername;
        int suffix = 1;

        while (userDAO.findByUsername(username).isPresent()) {
            username = baseUsername + suffix;
            suffix++;
        }

        return username;
    }

    public String generateRandomPassword() {
        return new Random().ints(48, 122)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .mapToObj(i -> (char) i)
                .limit(10)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
}
