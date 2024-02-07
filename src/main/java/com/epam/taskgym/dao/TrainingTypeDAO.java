package com.epam.taskgym.dao;

import com.epam.taskgym.entity.TrainingType;
import com.epam.taskgym.storage.TrainingTypeInMemoryDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TrainingTypeDAO {

    private final TrainingTypeInMemoryDb db;

    @Autowired
    public TrainingTypeDAO(TrainingTypeInMemoryDb db) {
        this.db = db;
    }

    public Optional<TrainingType> findById(Long id) {
        return db.findById(id);
    }

    public TrainingType save(TrainingType trainingType) {
        return db.save(trainingType);
    }
}
