package com.epam.taskgym.service;

import com.epam.taskgym.dao.TrainerDAO;
import com.epam.taskgym.dao.UserDAO;
import com.epam.taskgym.dto.TrainerDTO;
import com.epam.taskgym.entity.Trainer;
import com.epam.taskgym.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import java.util.Optional;
import java.util.Random;

@Service
public class TrainerService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private TrainerDAO trainerDAO;

    public boolean authenticateTrainer(String username, String password) {
        Trainer trainer = trainerDAO.findByUsernameAndPassword(username, password);
        return trainer != null;
    }

    public TrainerDTO registerTrainer(String firstName, String lastName, String specialization) {
        String username = generateUniqueUsername(firstName, lastName);
        String password = generateRandomPassword();

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(password);
        userDAO.save(user);

        Trainer trainer = new Trainer();
        trainer.setUserId(user.getId());
        trainer.setSpecialization(specialization);
        trainerDAO.save(trainer);

        TrainerDTO trainerDTO = new TrainerDTO();
        fillTrainerDTO(trainerDTO, user, trainer);

        return trainerDTO;
    }

    public Trainer findByUsername(String username) {
        Optional<User> user = userDAO.findByUsername(username);
        if (user.isPresent()) {
            return trainerDAO.findByUserId(user.get().getId());
        }
        return null;
    }

    public TrainerDTO getTrainer(String username) {
        Trainer trainer = trainerDAO.findByUsername(username);
        if (trainer != null) {
            Optional<User> userOptional = userDAO.findById(trainer.getUserId());
            if (userOptional.isPresent()) {
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
            String firstName = updates.get("firstName");
            user.setFirstName(firstName);
        }

        if (updates.containsKey("lastName")) {
            String lastName = updates.get("lastName");
            user.setLastName(lastName);
        }

        userDAO.update(user);

        if (updates.containsKey("specialization")) {
            String specialization = updates.get("specialization");
            trainer.setSpecialization(specialization);
        }

        trainerDAO.update(trainer);

        TrainerDTO trainerDTO = new TrainerDTO();
        fillTrainerDTO(trainerDTO, user, trainer);

        return trainerDTO;
    }

    public void deleteTrainer(String username) {
        Trainer trainerToDelete = findByUsername(username);
        if (trainerToDelete != null) {
            trainerDAO.deleteById(trainerToDelete.getId());
            userDAO.deleteById(trainerToDelete.getUserId());
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


    private String generateUniqueUsername(String firstName, String lastName) {
        String baseUsername = firstName + "." + lastName;
        String username = baseUsername;
        int suffix = 1;

        while (userDAO.findByUsername(username).isPresent()) {
            username = baseUsername + suffix;
            suffix++;
        }

        return username;
    }

    private String generateRandomPassword() {
        return new Random().ints(48, 122)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .mapToObj(i -> (char) i)
                .limit(10)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
}
