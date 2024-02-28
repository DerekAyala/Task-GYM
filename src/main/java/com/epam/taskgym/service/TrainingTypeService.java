package com.epam.taskgym.service;

import com.epam.taskgym.entity.TrainingType;
import com.epam.taskgym.repository.TrainingTypeRepository;
import com.epam.taskgym.exception.MissingAttributes;
import com.epam.taskgym.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class TrainingTypeService {

    private final TrainingTypeRepository trainingTypeRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingTypeService.class);

    public TrainingTypeService(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }

    public List<TrainingType> getAllTrainingTypes() {
        return trainingTypeRepository.findAll();
    }

    @Transactional
    public TrainingType RegisterTrainingType(String name) {
        if (name == null || name.isEmpty()) {
            LOGGER.error("Name is required");
            throw new MissingAttributes("Name is required");
        }
        TrainingType trainingType = new TrainingType();
        trainingType.setName(name);
        trainingTypeRepository.save(trainingType);
        LOGGER.info("Successfully registered training type: {}", trainingType.getName());
        return trainingType;
    }

    public TrainingType getTrainingTypeByName(String name) {
        LOGGER.info("Finding training type by name: {}", name);
        Optional<TrainingType> trainingType = trainingTypeRepository.findByName(name);
        if (trainingType.isEmpty()) {
            LOGGER.error("Training type with name {} not found", name);
            throw new NotFoundException("Training type with name {" + name + "} not found");
        }
        return trainingType.get();
    }
}
