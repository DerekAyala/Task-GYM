package com.epam.taskgym.service;

import com.epam.taskgym.dao.TrainerDAO;
import com.epam.taskgym.dao.UserDAO;
import com.epam.taskgym.entity.Trainer;
import com.epam.taskgym.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public void registerTrainer(String firstName, String lastName, String specialization) {
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
    }

    private String generateUniqueUsername(String firstName, String lastName) {
        String baseUsername = firstName + "." + lastName;
        String username = baseUsername;
        int suffix = 1;

        while(userDAO.findByUsername(username).isPresent()) {
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
