package com.epam.taskgym.dao;

import com.epam.taskgym.entity.Training;
import com.epam.taskgym.storage.TrainingInMemoryDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TrainingDAO {
    private final TrainingInMemoryDb db;

    @Autowired
    public TrainingDAO(TrainingInMemoryDb db) {
        this.db = db;
    }

    public Optional<Training> findById(Long id) {
        return db.findById(id);
    }

    public Training save(Training training) {
        return db.save(training);
    }
}
