package com.epam.taskgym.dao;

import com.epam.taskgym.entity.Trainee;
import com.epam.taskgym.entity.Trainer;
import com.epam.taskgym.entity.User;
import com.epam.taskgym.service.CurrentUserContext;
import com.epam.taskgym.storage.TrainerInMemoryDb;
import com.epam.taskgym.storage.UserInMemoryDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Repository
public class TrainerDAO {
    private final TrainerInMemoryDb db;
    private final UserInMemoryDb userDb;
    private CurrentUserContext currentUserContext;

    @Autowired
    public TrainerDAO(TrainerInMemoryDb db, CurrentUserContext currentUserContext, UserInMemoryDb userDb) {
        this.db = db;
        this.currentUserContext = currentUserContext;
        this.userDb = userDb;
    }

    public Optional<Trainer> findById(Long id) {
        return db.findById(id);
    }

    public List<Trainer> findAll() {
        return new ArrayList<>(db.findAll());
    }

    public Trainer save(Trainer trainer) {
        return db.save(trainer);
    }

    public void deleteById(Long id) {
        db.deleteById(id);
    }

    public Trainer update(Trainer trainer) {
        return db.update(trainer);
    }

    public Trainer login(Trainer trainer) {
        User user = userDb.findById(trainer.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + trainer.getUserId()));
        currentUserContext.setCurrentUser(user);
        currentUserContext.setUserType("Trainee");
        return trainer;
    }

    public Trainer findByUserId(Long userId) {
        for (Trainer trainer : db.findAll()) {
            if (trainer.getUserId().equals(userId)) {
                return trainer;
            }
        }
        return null;
    }

    public Trainer findByUsername(String username) {
        for (Trainer trainer : db.findAll()) {
            Optional<User> userOptional = userDb.findById(trainer.getUserId());
            if (userOptional.isPresent() && userOptional.get().getUsername().equals(username)) {
                return trainer;
            }
        }
        return null;
    }

    public Trainer findByUsernameAndPassword(String username, String password) {
        for (Trainer trainer : db.findAll()) {
            User user = userDb.findById(trainer.getUserId())
                    .orElse(null);
            if (user != null && user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return trainer;
            }
        }
        return null;
    }
}
