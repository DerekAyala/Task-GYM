package com.epam.taskgym.service;

import com.epam.taskgym.dao.TraineeDAO;
import com.epam.taskgym.dao.TrainerDAO;
import com.epam.taskgym.dao.UserDAO;
import com.epam.taskgym.dto.TrainerDTO;
import com.epam.taskgym.entity.Trainer;
import com.epam.taskgym.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import java.util.Optional;

@Service
public class TrainerService {


    private UserDAO userDAO;
    private TrainerDAO trainerDAO;

    private TraineeDAO traineeDAO;

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerService.class);

    @Autowired
    public TrainerService(UserDAO userDAO, TrainerDAO trainerDAO, TraineeDAO traineeDAO) {
        this.userDAO = userDAO;
        this.trainerDAO = trainerDAO;
        this.traineeDAO = traineeDAO;
    }

    public boolean authenticateTrainer(String username, String password) {
        Trainer trainer = trainerDAO.findByUsernameAndPassword(username, password);
        return trainer != null;
    }

    public TrainerDTO registerTrainer(String firstName, String lastName, String specialization) {
        TraineeService traineeService = new TraineeService(userDAO, traineeDAO);
        String username = traineeService.generateUniqueUsername(firstName.toLowerCase(), lastName.toLowerCase());
        String password = traineeService.generateRandomPassword();

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(password);
        userDAO.save(user);
        LOGGER.info("User saved with ID: {}", user.getId());

        Trainer trainer = new Trainer();
        trainer.setUserId(user.getId());
        trainer.setSpecialization(specialization);
        trainerDAO.save(trainer);
        LOGGER.info("Trainer saved with ID: {}", trainer.getId());

        TrainerDTO trainerDTO = new TrainerDTO();
        fillTrainerDTO(trainerDTO, user, trainer);

        return trainerDTO;
    }

    public Trainer findByUsername(String username) {
        Optional<User> user = userDAO.findByUsername(username);
        if (user.isPresent()) {
            LOGGER.info("Trainer was found");
            return trainerDAO.findByUserId(user.get().getId());
        }
        return null;
    }

    public TrainerDTO getTrainer(String username) {
        Trainer trainer = trainerDAO.findByUsername(username);
        if (trainer != null) {
            LOGGER.info("Trainer was found");
            Optional<User> userOptional = userDAO.findById(trainer.getUserId());
            if (userOptional.isPresent()) {
                LOGGER.info("User was found");
                User user = userOptional.get();

                TrainerDTO trainerDTO = new TrainerDTO();
                fillTrainerDTO(trainerDTO, user, trainer);

                return trainerDTO;
            }
        }
        return null;
    }

    public TrainerDTO updateTrainer(String username, Map<String, String> updates) {
        User user = userDAO.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Trainer trainer = trainerDAO.findByUserId(user.getId());
        if (trainer == null) {
            throw new RuntimeException("Trainer not found");
        }

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

        if (updates.containsKey("specialization")) {
            LOGGER.info("updates contains specialization");
            String specialization = updates.get("specialization");
            trainer.setSpecialization(specialization);
        }

        trainerDAO.update(trainer);
        LOGGER.info("Trainer updated with ID: {}", trainer.getId());

        TrainerDTO trainerDTO = new TrainerDTO();
        fillTrainerDTO(trainerDTO, user, trainer);

        return trainerDTO;
    }

    public void deleteTrainer(String username) {
        Trainer trainerToDelete = findByUsername(username);
        if (trainerToDelete != null) {
            LOGGER.info("Trainer was found");
            trainerDAO.deleteById(trainerToDelete.getId());
            LOGGER.info("Trainer was deleted");
            userDAO.deleteById(trainerToDelete.getUserId());
            LOGGER.info("User was deleted");
        }
    }

    private void fillTrainerDTO(TrainerDTO trainerDTO, User user, Trainer trainer) {
        trainerDTO.setUserId(user.getId());
        trainerDTO.setUsername(user.getUsername());
        trainerDTO.setPassword(user.getPassword());
        trainerDTO.setFirstName(user.getFirstName());
        trainerDTO.setLastName(user.getLastName());
        trainerDTO.setTrainerId(trainer.getId());
        trainerDTO.setSpecialization(trainer.getSpecialization());
    }
}
