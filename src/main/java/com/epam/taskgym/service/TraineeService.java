package com.epam.taskgym.service;

import com.epam.taskgym.dao.TraineeDAO;
import com.epam.taskgym.dao.UserDAO;
import com.epam.taskgym.entity.Trainee;
import com.epam.taskgym.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class TraineeService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private TraineeDAO traineeDAO;

    public boolean authenticateTrainee(String username, String password) {
        Trainee trainee = traineeDAO.findByUsernameAndPassword(username, password);
        return trainee != null;
    }

    public User registerTrainee(String firstName, String lastName, String dateOfBirth, String address) {
        String username = generateUniqueUsername(firstName, lastName);
        String password = generateRandomPassword();

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(password);

        userDAO.save(user);

        Trainee trainee = new Trainee();
        trainee.setUserId(user.getId());
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);

        traineeDAO.save(trainee);
        return user;
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
