package com.epam.taskgym.service;

import com.epam.taskgym.dto.TrainerDTO;
import com.epam.taskgym.entity.Trainer;
import com.epam.taskgym.entity.TrainingType;
import com.epam.taskgym.entity.User;
import com.epam.taskgym.repository.TrainerRepository;
import com.epam.taskgym.service.exception.FailAuthenticateException;
import com.epam.taskgym.service.exception.MissingAttributes;
import com.epam.taskgym.service.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;


@Service
public class TrainerService {

    @Autowired
    private TrainerRepository trainerRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private TrainingTypeService trainingTypeService;

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerService.class);


    private void authenticateTrainer(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new MissingAttributes("Username and password are required");
        }
        Trainer trainer = getTrainerByUsername(username);
        if (!trainer.getUser().getPassword().equals(password)) {
            throw new FailAuthenticateException("Fail to authenticate: Password and username do not match");
        }
    }

    public Trainer getTrainerByUsername(String username) {
        Optional<Trainer> trainer = trainerRepository.findByUserUsername(username);
        if (trainer.isEmpty()) {
            throw new NotFoundException("Trainer with username {" + username + "} not found");
        }
        return trainer.get();
    }

    @Transactional
    public TrainerDTO registerTrainer(Map<String, String> trainerDetails) {
        validateTrainerDetails(trainerDetails);
        User user = userService.createUser(trainerDetails);
        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setSpecialization(validateSpecialization(trainerDetails));
        trainerRepository.save(trainer);
        return fillTrainerDTO(user, trainer);
    }

    @Transactional
    public TrainerDTO updateTrainer(Map<String, String> trainerDetails, String username, String password) {
        authenticateTrainer(username, password);
        validateTrainerDetails(trainerDetails);
        Trainer trainer = getTrainerByUsername(username);
        User user = userService.updateUser(trainerDetails, trainer.getUser());
        trainer.setUser(user);
        trainer.setSpecialization(validateSpecialization(trainerDetails));
        trainerRepository.save(trainer);
        return fillTrainerDTO(user, trainer);
    }

    @Transactional
    public void updatePasssword(String username, String password, String newPassword) {
        authenticateTrainer(username, password);
        TraineeService.validatePassword(newPassword);
        Trainer trainer = getTrainerByUsername(username);
        User user = trainer.getUser();
        user.setPassword(newPassword);
        userService.saveUser(user);
        trainer.setUser(user);
        trainerRepository.save(trainer);
    }

    private TrainerDTO fillTrainerDTO(User user, Trainer trainer) {
        return new TrainerDTO(user.getUsername(), user.getPassword(), user.getFirstName(), user.getLastName(), trainer.getSpecialization());
    }

    private TrainingType validateSpecialization(Map<String, String> trainerDetails) {
        if ((!trainerDetails.containsKey("specialization") || trainerDetails.get("specialization").isEmpty())) {
            throw new MissingAttributes("specialization is required");
        }
        return trainingTypeService.getTrainingTypeByName(trainerDetails.get("specialization"));

    }

    private void validateTrainerDetails(Map<String, String> trainerDetails) {
        if (trainerDetails == null || trainerDetails.isEmpty()) {
            throw new MissingAttributes("Trainer details cannot be null or empty");
        }
    }
}
