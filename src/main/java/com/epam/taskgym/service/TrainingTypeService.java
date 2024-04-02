package com.epam.taskgym.service;

import com.epam.taskgym.entity.TrainingType;
import com.epam.taskgym.helpers.Validations;
import com.epam.taskgym.repository.TrainingTypeRepository;
import com.epam.taskgym.exception.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TrainingTypeService {

    private final TrainingTypeRepository trainingTypeRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingTypeService.class);

    public List<TrainingType> getAllTrainingTypes() {
        LOGGER.info("Transaction Id: {}, Method: {}, Finding all training types", MDC.get("transactionId"), MDC.get("MethodName"));
        return trainingTypeRepository.findAll();
    }

    @Transactional
    public TrainingType RegisterTrainingType(String name) {
        Validations.validateTrainingTypeDetails(name);
        TrainingType trainingType = new TrainingType();
        trainingType.setName(name);
        trainingTypeRepository.save(trainingType);
        LOGGER.info("Successfully registered training type: {}", trainingType.getName());
        return trainingType;
    }

    public TrainingType getTrainingTypeByName(String name) {
        LOGGER.info("Transaction Id: {}, Method: {}, Finding training type by name: {}", MDC.get("transactionId"), MDC.get("MethodName"), name);
        Optional<TrainingType> trainingType = trainingTypeRepository.findByName(name);
        if (trainingType.isEmpty()) {
            LOGGER.error("Transaction Id: {}, Method: {}, Training type with name {} not found", MDC.get("transactionId"), MDC.get("MethodName"), name);
            throw new NotFoundException("Training type with name {" + name + "} not found");
        }
        return trainingType.get();
    }
}
