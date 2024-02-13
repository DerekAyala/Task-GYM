package com.epam.taskgym.service;

import com.epam.taskgym.entity.TrainingType;
import com.epam.taskgym.repository.TrainingTypeRepository;
import com.epam.taskgym.service.exception.MissingAttributes;
import com.epam.taskgym.service.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainingTypeService {

    @Autowired
    private TrainingTypeRepository trainingTypeRepository;

    public List<TrainingType> getAllTrainingTypes() {
        return trainingTypeRepository.findAll();
    }

    @Transactional
    public TrainingType RegisterTrainingType(String name) {
        if (name == null || name.isEmpty()) {
            throw new MissingAttributes("Name is required");
        }
        TrainingType trainingType = new TrainingType();
        trainingType.setName(name);
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
