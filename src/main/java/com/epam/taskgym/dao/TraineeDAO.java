package com.epam.taskgym.dao;

import com.epam.taskgym.entity.Trainee;
import com.epam.taskgym.storage.TraineeInMemoryDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TraineeDAO {

    private final TraineeInMemoryDb db;

    @Autowired
    public TraineeDAO(TraineeInMemoryDb db) {
        this.db = db;
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
}
