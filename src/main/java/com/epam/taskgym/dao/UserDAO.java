package com.epam.taskgym.dao;

import com.epam.taskgym.entity.User;
import com.epam.taskgym.storage.UserInMemoryDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserDAO {
    private final UserInMemoryDb db;

    @Autowired
    public UserDAO(UserInMemoryDb db) {
        this.db = db;
    }

    public Optional<User> findById(Long id) {
        return db.findById(id);
    }

    public User save(User user) {
        return db.save(user);
    }

    public void deleteById(Long id) {
        db.deleteById(id);
    }

    public User update(User user) {
        return db.update(user);
    }
}
