package com.epam.taskgym.service;

import com.epam.taskgym.entity.TrainingType;
import com.epam.taskgym.repository.TrainingTypeRepository;
import com.epam.taskgym.service.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainingTypeService {

    @Autowired
    private TrainingTypeRepository trainingTypeRepository;

    public List<TrainingType> getAllTrainingTypes() {
        return trainingTypeRepository.findAll();
    }

    public Optional<TrainingType> getTrainingTypeById(Long id) {
        return trainingTypeRepository.findById(id);
    }

    public TrainingType RegisterTrainingType(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        TrainingType trainingType = new TrainingType();
        return trainingTypeRepository.save(trainingType);
    }

    public TrainingType getTrainingTypeByName(String name) {
        Optional<TrainingType> trainingType = trainingTypeRepository.findByName(name);
        if (trainingType.isEmpty()) {
            throw new NotFoundException("Training type with name {" + name + "} not found");
        }
        return trainingType.get();
    }
}
