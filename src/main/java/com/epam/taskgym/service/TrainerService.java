package com.epam.taskgym.service;

import com.epam.taskgym.dto.TrainerDTO;
import com.epam.taskgym.entity.Trainer;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Service
public class TrainerService {



    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerService.class);


    public boolean authenticateTrainer(String username, String password) {
        /*
        Trainer trainer = trainerDAO.findByUsernameAndPassword(username, password);
        return trainer != null;
        */
        return true;
    }

    public TrainerDTO registerTrainer(String firstName, String lastName, String specialization) {
        /*
        TraineeService traineeService = new TraineeService(userDAO, traineeDAO);
        String username = traineeService.generateUniqueUsername(firstName.toLowerCase(), lastName.toLowerCase());
        String password = traineeService.generateRandomPassword();

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(password);
        userDAO.save(user);
        LOGGER.info("User Trainer saved with ID: {}", user.getId());

        Trainer trainer = new Trainer();
        trainer.setUserId(user.getId());
        trainer.setSpecialization(specialization);
        trainerDAO.save(trainer);
        LOGGER.info("Trainer saved with ID: {}", trainer.getId());

        TrainerDTO trainerDTO = new TrainerDTO();
        fillTrainerDTO(trainerDTO, user, trainer);

        return trainerDTO;
        */
        return new TrainerDTO();
    }

    public Trainer findByUsername(String username) {
        /*
        Optional<User> user = userDAO.findByUsername(username);
        if (user.isPresent()) {
            LOGGER.info("Trainer was found by username");
            return trainerDAO.findByUserId(user.get().getId());
        }
        */
        return null;
    }

    public TrainerDTO getTrainer(String username) {
        /*
        Trainer trainer = trainerDAO.findByUsername(username);
        if (trainer != null) {
            LOGGER.info("Trainer was found by username");
            Optional<User> userOptional = userDAO.findById(trainer.getUserId());
            if (userOptional.isPresent()) {
                LOGGER.info("User is present");
                User user = userOptional.get();

                TrainerDTO trainerDTO = new TrainerDTO();
                fillTrainerDTO(trainerDTO, user, trainer);

                return trainerDTO;
            }
        }
        */
        return null;
    }

    public TrainerDTO updateTrainer(String username, Map<String, String> updates) {
        /*
        User user = userDAO.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Trainer trainer = trainerDAO.findByUserId(user.getId());
        if (trainer == null) {
            throw new RuntimeException("Trainer not found by user id");
        }

        if (updates.containsKey("firstName")) {
            LOGGER.info("Trainer updates contains firstName");
            String firstName = updates.get("firstName");
            user.setFirstName(firstName);
        }

        if (updates.containsKey("lastName")) {
            LOGGER.info("Trainer updates contains lastName");
            String lastName = updates.get("lastName");
            user.setLastName(lastName);
        }

        userDAO.update(user);

        if (updates.containsKey("specialization")) {
            LOGGER.info("Trainer updates contains specialization");
            String specialization = updates.get("specialization");
            trainer.setSpecialization(specialization);
        }

        trainerDAO.update(trainer);
        LOGGER.info("Trainer updated with ID: {}", trainer.getId());

        TrainerDTO trainerDTO = new TrainerDTO();
        fillTrainerDTO(trainerDTO, user, trainer);
         */
        return new TrainerDTO();

    }

    public void deleteTrainer(String username) {
        /*
        Trainer trainerToDelete = findByUsername(username);
        if (trainerToDelete != null) {
            trainerDAO.deleteById(trainerToDelete.getId());
            userDAO.deleteById(trainerToDelete.getUserId());
            LOGGER.info("User and Trainer were deleted");
        }
        */
    }
    /*

    private void fillTrainerDTO(TrainerDTO trainerDTO, User user, Trainer trainer) {
        trainerDTO.setUserId(user.getId());
        trainerDTO.setUsername(user.getUsername());
        trainerDTO.setPassword(user.getPassword());
        trainerDTO.setFirstName(user.getFirstName());
        trainerDTO.setLastName(user.getLastName());
        trainerDTO.setTrainerId(trainer.getId());
        trainerDTO.setSpecialization(trainer.getSpecialization());
    }
    */
}
