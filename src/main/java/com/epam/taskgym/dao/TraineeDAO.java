package com.epam.taskgym.dao;

import com.epam.taskgym.entity.Trainee;
import com.epam.taskgym.entity.User;
import com.epam.taskgym.storage.TraineeInMemoryDb;
import com.epam.taskgym.storage.UserInMemoryDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Repository
public class TraineeDAO {

    private final TraineeInMemoryDb db;
    private final UserInMemoryDb userDb;

    @Autowired
    public TraineeDAO(TraineeInMemoryDb db, UserInMemoryDb userDb) {
        this.db = db;
        this.userDb = userDb;
    }


    public Optional<Trainee> findById(Long id) {
        return db.findById(id);
    }

    public List<Trainee> findAll() {
        return new ArrayList<>(db.findAll());
    }

    public Trainee save(Trainee trainee) {
        return db.save(trainee);
    }

    public void deleteById(Long id) {
        db.deleteById(id);
    }

    public Trainee update(Trainee trainee) {
        return db.update(trainee);
    }

    public Trainee login(Trainee trainee) {
        User user = userDb.findById(trainee.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + trainee.getUserId()));
        return trainee;
    }

    public Trainee findByUserId(Long userId) {
        for (Trainee trainee : db.findAll()) {
            if (trainee.getUserId().equals(userId)) {
                return trainee;
            }
        }
        return null;
    }

    public Trainee findByUsername(String username) {
        for (Trainee trainee : db.findAll()) {
            Optional<User> userOptional = userDb.findById(trainee.getUserId());
            if (userOptional.isPresent() && userOptional.get().getUsername().equals(username)) {
                return trainee;
            }
        }
        return null;
    }


    public Trainee findByUsernameAndPassword(String username, String password) {
        for (Trainee trainee : db.findAll()) {
            User user = userDb.findById(trainee.getUserId())
                    .orElse(null);
            if (user != null && user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return trainee;
            }
        }
        return null;
    }
}
