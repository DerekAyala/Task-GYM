package com.epam.microservice.service;

import com.epam.microservice.entity.TrainingMonth;
import com.epam.microservice.entity.TrainingWork;
import com.epam.microservice.entity.TrainingYears;
import com.epam.microservice.exception.NotFoundException;
import com.epam.microservice.model.TrainingRequest;
import com.epam.microservice.repository.TrainingMonthRepository;
import com.epam.microservice.repository.TrainingWorkRepository;
import com.epam.microservice.repository.TrainingYearsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.epam.microservice.helper.Validations.*;

@Service
@RequiredArgsConstructor
public class TrainingWorkService {
    private final TrainingWorkRepository trainingWorkRepository;
    private final TrainingYearsRepository trainingYearsRepository;
    private final TrainingMonthRepository trainingMonthRepository;

    public void acceptTrainerWork(TrainingRequest trainingRequest) {
        validateTrainingRequest(trainingRequest);
        validateAction(trainingRequest);
        if (trainingRequest.getAction().equalsIgnoreCase("add")) {
            addTrainingWork(trainingRequest);
        } else {
            deleteTrainingWork(trainingRequest);
        }
    }

    public void addTrainingWork(TrainingRequest trainingRequest) {
        validateTrainingRequestForAdd(trainingRequest);
        if (trainingWorkRepository.findByUsername(trainingRequest.getUsername()).isEmpty()) {
            createTrainingWork(trainingRequest);
        } else {
            updateTrainingWork(trainingRequest);
        }
    }

    public void deleteTrainingWork(TrainingRequest trainingRequest) {
        validateTrainingRequestForDelete(trainingRequest);
        Optional<TrainingWork> OPtrainingWork = trainingWorkRepository.findByUsername(trainingRequest.getUsername());
        if(OPtrainingWork.isEmpty()) {
            throw new NotFoundException("Training work not found");
        }
        TrainingWork trainingWork = OPtrainingWork.get();
        deleteTrainingYears(trainingWork.getYears());
        trainingWorkRepository.delete(trainingWork);
    }

    private void createTrainingWork(TrainingRequest trainingRequest) {
        TrainingWork trainingWork = new TrainingWork();
        trainingWork.setFirstName(trainingRequest.getFirstName());
        trainingWork.setLastName(trainingRequest.getLastName());
        trainingWork.setStatus(trainingRequest.getIsActive());
        trainingWork.setUsername(trainingRequest.getUsername());
        trainingWork.setYears(List.of(createTrainingYears(trainingRequest)));
        trainingWorkRepository.save(trainingWork);
    }

    private void updateTrainingWork(TrainingRequest trainingRequest) {
        TrainingWork trainingWork = trainingWorkRepository.findByUsername(trainingRequest.getUsername()).get();
        trainingWork.setYears(updateTrainingYears(trainingWork, trainingRequest));
        trainingWorkRepository.save(trainingWork);
    }

    private TrainingYears createTrainingYears(TrainingRequest trainingRequest) {
        TrainingYears trainingYears = new TrainingYears();
        trainingYears.setYear(String.valueOf(trainingRequest.getDate().getYear()));
        trainingYears.setMonths(List.of(createTrainingMonth(trainingRequest)));
        trainingYearsRepository.save(trainingYears);
        return trainingYears;
    }

    private List<TrainingYears> updateTrainingYears(TrainingWork trainingWork, TrainingRequest trainingRequest) {
        List<TrainingYears> trainingYears = trainingWork.getYears();
        boolean present = false;
        for (TrainingYears year : trainingYears) {
            if (year.getYear().equals(String.valueOf(trainingRequest.getDate().getYear()))) {
                year.setMonths(updateTrainingMonth(year, trainingRequest));
                trainingYearsRepository.save(year);
                present = true;
                break;
            }
        }
        if (!present) {
            trainingYears.add(createTrainingYears(trainingRequest));
        }
        return trainingYears;
    }

    private void deleteTrainingYears(List<TrainingYears> trainingYears) {
        for (TrainingYears year : trainingYears) {
            deleteTrainingMonth(year.getMonths());
        }
        trainingYearsRepository.deleteAll(trainingYears);
    }

    private TrainingMonth createTrainingMonth(TrainingRequest trainingRequest) {
        TrainingMonth trainingMonth = new TrainingMonth();
        trainingMonth.setMonth(String.valueOf(trainingRequest.getDate().getMonth()));
        trainingMonth.setHours(trainingRequest.getDuration());
        trainingMonthRepository.save(trainingMonth);
        return trainingMonth;
    }

    private List<TrainingMonth> updateTrainingMonth(TrainingYears trainingYears, TrainingRequest trainingRequest) {
        List<TrainingMonth> trainingMonths = trainingYears.getMonths();
        boolean present = false;
        for (TrainingMonth month : trainingMonths) {
            if (month.getMonth().equals(String.valueOf(trainingRequest.getDate().getMonth()))) {
                month.setHours(month.getHours() + trainingRequest.getDuration());
                trainingMonthRepository.save(month);
                present = true;
                break;
            }
        }
        if (!present) {
            trainingMonths.add(createTrainingMonth(trainingRequest));
        }
        return trainingMonths;
    }

    private void deleteTrainingMonth(List<TrainingMonth> trainingMonths) {
        trainingMonthRepository.deleteAll(trainingMonths);
    }
}
