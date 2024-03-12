package com.epam.microservice.service;

import com.epam.microservice.entity.TrainingWork;
import com.epam.microservice.entity.TrainingYears;
import com.epam.microservice.model.TrainingRequest;
import com.epam.microservice.repository.TrainingMonthRepository;
import com.epam.microservice.repository.TrainingWorkRepository;
import com.epam.microservice.repository.TrainingYearsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainingWorkService {
    private final TrainingWorkRepository trainingWorkRepository;
    private final TrainingYearsRepository trainingYearsRepository;
    private final TrainingMonthRepository trainingMonthRepository;

    public void addTrainingWork(TrainingRequest trainingRequest) {
        TrainingWork trainingWork = new TrainingWork();
        trainingWork.setFirstName(trainingRequest.getFirstName());
        trainingWork.setLastName(trainingRequest.getLastName());
        trainingWork.setStatus(trainingRequest.getIsActive());
    }

    private TrainingYears addTrainingYear(TrainingRequest trainingRequest) {
        TrainingYears trainingYears = new TrainingYears();
        trainingYears.setYear(String.valueOf(trainingRequest.getDate().getYear()));
        return trainingYearsRepository.save(trainingYears);
    }
}
