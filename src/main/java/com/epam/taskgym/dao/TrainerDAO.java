package com.epam.taskgym.dao;

import com.epam.taskgym.entity.Trainer;
import com.epam.taskgym.storage.TrainerInMemoryDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TrainerDAO {
    private final TrainerInMemoryDb db;

    @Autowired
    public TrainerDAO(TrainerInMemoryDb db) {
        this.db = db;
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
}
