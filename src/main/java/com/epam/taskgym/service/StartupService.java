package com.epam.taskgym.service;

import com.epam.taskgym.entity.TrainingType;
import com.epam.taskgym.repository.TrainingTypeRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class StartupService {

    private final TrainingTypeRepository trainingTypeRepository;

    public StartupService(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @PostConstruct
    public void init() {
        insertDefaultTrainingTypes();
    }

    private void insertDefaultTrainingTypes() {
        List<String> defaultTypes = Arrays.asList("Functional Training", "Mobility Training", "Strength Training", "Balance Training", "Agility Training");

        defaultTypes.forEach(type -> {
            if (!trainingTypeRepository.existsByName(type)) {
                TrainingType trainingType = new TrainingType();
                trainingType.setName(type);
                trainingTypeRepository.save(trainingType);
            }
        });
    }
}

