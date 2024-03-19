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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import static com.epam.microservice.helper.Validations.*;

@Service
@RequiredArgsConstructor
public class TrainingWorkService {
    private final TrainingWorkRepository trainingWorkRepository;
    private final TrainingYearsRepository trainingYearsRepository;
    private final TrainingMonthRepository trainingMonthRepository;

    private final Logger LOGGER = LoggerFactory.getLogger(TrainingWorkService.class);

    public void acceptTrainerWork(TrainingRequest trainingRequest) {
        LOGGER.info("Accepting training work ADD or DELETE request");
        validateTrainingRequest(trainingRequest);
        validateAction(trainingRequest);
        if (trainingRequest.getAction().equalsIgnoreCase("add")) {
            addTrainingWork(trainingRequest);
        } else {
            deleteTrainingWork(trainingRequest);
        }
    }

    public void addTrainingWork(TrainingRequest trainingRequest) {
        LOGGER.info("Adding training work");
        validateTrainingRequestForAdd(trainingRequest);
        LOGGER.info("Finding training work by username: {}", trainingRequest.getUsername());
        if (trainingWorkRepository.findByUsername(trainingRequest.getUsername()).isEmpty()) {
            LOGGER.info("Creating new training work");
            createTrainingWork(trainingRequest);
        } else {
            LOGGER.info("Updating existing training work");
            updateTrainingWork(trainingRequest);
        }
    }

    public void deleteTrainingWork(TrainingRequest trainingRequest) {
        LOGGER.info("Deleting training work");
        validateTrainingRequestForDelete(trainingRequest);
        Optional<TrainingWork> OptionalTrainingWork = trainingWorkRepository.findByUsername(trainingRequest.getUsername());
        if(OptionalTrainingWork.isEmpty()) {
            throw new NotFoundException("Training work not found");
        }
        TrainingWork trainingWork = OptionalTrainingWork.get();
        List<TrainingYears> trainingYears = trainingWork.getYears();
        for (TrainingYears year : trainingYears) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(trainingRequest.getDate());
            if (year.getYearNumber().equals(String.valueOf(calendar.get(Calendar.YEAR)))) {
                List<TrainingMonth> trainingMonths = year.getMonths();
                for (TrainingMonth month : trainingMonths) {
                    if (month.getMonthName().equals(String.valueOf(calendar.get(Calendar.MONTH)))) {
                        int result = month.getHours() - trainingRequest.getDuration();
                        if (result == 0){
                            trainingMonths.remove(month);
                            trainingMonthRepository.delete(month);
                        } else {
                            month.setHours(result);
                            trainingMonthRepository.save(month);
                        }
                        break;
                    }
                }
                if (trainingMonths.isEmpty()) {
                    trainingYears.remove(year);
                    trainingYearsRepository.delete(year);
                } else {
                    year.setMonths(trainingMonths);
                    trainingYearsRepository.save(year);
                }
                break;
            }
        }
        if (trainingYears.isEmpty()) {
            trainingWorkRepository.delete(trainingWork);
        } else {
            trainingWork.setYears(trainingYears);
            trainingWorkRepository.save(trainingWork);
        }
    }

    private void createTrainingWork(TrainingRequest trainingRequest) {
        TrainingWork trainingWork = new TrainingWork();
        trainingWork.setFirstName(trainingRequest.getFirstName());
        trainingWork.setLastName(trainingRequest.getLastName());
        trainingWork.setStatus(trainingRequest.getIsActive());
        trainingWork.setUsername(trainingRequest.getUsername());
        List<TrainingYears> years = List.of(createTrainingYears(trainingRequest));
        trainingWork.setYears(years);
        trainingWorkRepository.save(trainingWork);
        LOGGER.info("Successfully created training work: {}", trainingWork);
    }

    private void updateTrainingWork(TrainingRequest trainingRequest) {
        TrainingWork trainingWork = trainingWorkRepository.findByUsername(trainingRequest.getUsername()).get();
        List<TrainingYears> years = updateTrainingYears(trainingWork, trainingRequest);
        trainingWork.setYears(years);
        trainingWorkRepository.save(trainingWork);
        LOGGER.info("Successfully updated training work: {}", trainingWork);
    }

    private TrainingYears createTrainingYears(TrainingRequest trainingRequest) {
        TrainingYears trainingYears = new TrainingYears();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(trainingRequest.getDate());
        trainingYears.setYearNumber(String.valueOf(calendar.get(Calendar.YEAR)));
        List<TrainingMonth> months = List.of(createTrainingMonth(trainingRequest));
        trainingYears.setMonths(months);
        trainingYearsRepository.save(trainingYears);
        return trainingYears;
    }

    private List<TrainingYears> updateTrainingYears(TrainingWork trainingWork, TrainingRequest trainingRequest) {
        List<TrainingYears> trainingYears = trainingWork.getYears();
        boolean present = false;
        for (TrainingYears year : trainingYears) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(trainingRequest.getDate());
            if (year.getYearNumber().equals(String.valueOf(calendar.get(Calendar.YEAR)))) {
                List<TrainingMonth> months = updateTrainingMonth(year, trainingRequest);
                year.setMonths(months);
                trainingYearsRepository.save(year);
                present = true;
                break;
            }
        }
        if (!present) {
            TrainingYears ty = createTrainingYears(trainingRequest);
            trainingYears.add(ty);
        }
        return trainingYears;
    }

    private TrainingMonth createTrainingMonth(TrainingRequest trainingRequest) {
        TrainingMonth trainingMonth = new TrainingMonth();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(trainingRequest.getDate());
        trainingMonth.setMonthName(String.valueOf(calendar.get(Calendar.MONTH)));
        trainingMonth.setHours(trainingRequest.getDuration());
        trainingMonthRepository.save(trainingMonth);
        return trainingMonth;
    }

    private List<TrainingMonth> updateTrainingMonth(TrainingYears trainingYears, TrainingRequest trainingRequest) {
        List<TrainingMonth> trainingMonths = trainingYears.getMonths();
        boolean present = false;
        for (TrainingMonth month : trainingMonths) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(trainingRequest.getDate());
            if (month.getMonthName().equals(String.valueOf(calendar.get(Calendar.MONTH)))) {
                int hours = month.getHours() + trainingRequest.getDuration();
                month.setHours(hours);
                trainingMonthRepository.save(month);
                present = true;
                break;
            }
        }
        if (!present) {
            TrainingMonth trainingMonth = createTrainingMonth(trainingRequest);
            trainingMonths.add(trainingMonth);
        }
        return trainingMonths;
    }
}
