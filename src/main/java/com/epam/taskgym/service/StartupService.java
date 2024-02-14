package com.epam.taskgym.service;

import com.epam.taskgym.entity.TrainingType;
import com.epam.taskgym.repository.TrainingTypeRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class StartupService {

    @Autowired
    private TrainingTypeRepository trainingTypeRepository;
    @Autowired
    private TrainingTypeService trainingTypeService;

    @PostConstruct
    public void init() {
        insertDefaultTrainingTypes();
    }

    private void insertDefaultTrainingTypes() {
        List<String> defaultTypes = Arrays.asList("Functional Training", "Mobility Training", "Strength Training", "Balance Training", "Agility Training", "Stretching");

        defaultTypes.forEach(type -> {
            if (!trainingTypeRepository.existsByName(type)) {
                trainingTypeService.RegisterTrainingType(type);
            }
        });
    }
}

