package com.epam.taskgym.service;

import com.epam.taskgym.dto.TrainerDTO;
import com.epam.taskgym.entity.Trainer;
import com.epam.taskgym.entity.TrainingType;
import com.epam.taskgym.entity.User;
import com.epam.taskgym.repository.TrainerRepository;
import com.epam.taskgym.service.exception.FailAuthenticateException;
import com.epam.taskgym.service.exception.MissingAttributes;
import com.epam.taskgym.service.exception.NotFoundException;
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


    public void authenticateTrainer(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new MissingAttributes("Username and password are required");
        }
        Trainer trainer = getTrainerByUsername(username);
        if (!trainer.getUser().getPassword().equals(password)) {
            throw new FailAuthenticateException("Fail to authenticate");
        }
    }

    public Trainer getTrainerByUsername(String username) {
        Optional<Trainer> trainer = trainerRepository.findByUserUsername(username);
        if (trainer.isEmpty()) {
            throw new NotFoundException("Trainer with username {" + username + "} not found");
        }
        return trainer.get();
    }

    public TrainerDTO registerTrainer(Map<String, String> trainerDetails) {
        if (trainerDetails == null || trainerDetails.isEmpty()) {
            throw new MissingAttributes("Trainer details cannot be null or empty");
        }
        User user = userService.createUser(trainerDetails);
        Trainer trainer = new Trainer();
        trainer.setUser(user);
        if ((!trainerDetails.containsKey("specialization") || trainerDetails.get("specialization").isEmpty())) {
            throw new MissingAttributes("specialization is required");
        }
        TrainingType specialization = trainingTypeService.getTrainingTypeByName(trainerDetails.get("specialization"));
        trainer.setSpecialization(specialization);
        trainerRepository.save(trainer);
        return fillTrainerDTO(user, trainer);
    }

    public TrainerDTO updateTrainer(Map<String, String> trainerDetails, String username, String password) {
        if (trainerDetails == null || trainerDetails.isEmpty()) {
            throw new MissingAttributes("Trainer Update details cannot be null or empty");
        }
        authenticateTrainer(username, password);
        Trainer trainer = getTrainerByUsername(username);
        User user = userService.updateUser(trainerDetails, trainer.getUser());
        trainer.setUser(user);
        if (trainerDetails.containsKey("specialization") && !trainerDetails.get("specialization").isEmpty()) {
            TrainingType specialization = trainingTypeService.getTrainingTypeByName(trainerDetails.get("specialization"));
            trainer.setSpecialization(specialization);
        }
        trainerRepository.save(trainer);
        return fillTrainerDTO(user, trainer);
    }


    public boolean updatePasssword(String username, String password, String newPassword) {
        authenticateTrainer(username, password);
        Trainer trainer = getTrainerByUsername(username);
        User user = trainer.getUser();
        user.setPassword(newPassword);
        userService.saveUser(user);
        trainer.setUser(user);
        trainerRepository.save(trainer);
        return true;
    }

    private TrainerDTO fillTrainerDTO(User user, Trainer trainer) {
        return new TrainerDTO(user, user.getUsername(), user.getPassword(), user.getFirstName(), user.getLastName(), trainer, trainer.getSpecialization());
    }
}
