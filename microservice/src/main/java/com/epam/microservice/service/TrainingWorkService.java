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
import org.slf4j.MDC;
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
        validateTrainingRequest(trainingRequest);
        validateAction(trainingRequest);
        if (trainingRequest.getAction().equalsIgnoreCase("add")) {
            MDC.put("Action", "Add");
            addTrainingWork(trainingRequest);
        } else {
            MDC.put("Action", "Delete");
            deleteTrainingWork(trainingRequest);
        }
        MDC.remove("Action");
    }

    public void addTrainingWork(TrainingRequest trainingRequest) {
        validateTrainingRequestForAdd(trainingRequest);
        LOGGER.info("Transaction Id: {}, Action: {}, Adding training work", MDC.get("transactionId"), MDC.get("Action"));
        LOGGER.info("Transaction Id: {}, finding training work by username: {}", MDC.get("transactionId"), trainingRequest.getUsername());
        if (trainingWorkRepository.findByUsername(trainingRequest.getUsername()).isEmpty()) {
            LOGGER.info("Transaction Id: {}, Creating new training work", MDC.get("transactionId"));
            createTrainingWork(trainingRequest);
        } else {
            LOGGER.info("Transaction Id: {}, Updating training work", MDC.get("transactionId"));
            updateTrainingWork(trainingRequest);
        }
    }

    public void deleteTrainingWork(TrainingRequest trainingRequest) {
        LOGGER.info("Transaction Id: {}, Action: {}, Deleting training work", MDC.get("transactionId"), MDC.get("Action"));
        validateTrainingRequestForDelete(trainingRequest);
        LOGGER.info("Transaction Id: {}, finding training work by username: {}", MDC.get("transactionId"), trainingRequest.getUsername());
        Optional<TrainingWork> OptionalTrainingWork = trainingWorkRepository.findByUsername(trainingRequest.getUsername());
        if(OptionalTrainingWork.isEmpty()) {
            LOGGER.error("Transaction Id: {}, Training work not found", MDC.get("transactionId"));
            throw new NotFoundException("Training work not found");
        }
        TrainingWork trainingWork = OptionalTrainingWork.get();
        List<TrainingYears> trainingYears = trainingWork.getYears();
        for (TrainingYears year : trainingYears) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(trainingRequest.getDate());
            if (year.getYearNumber().equals(String.valueOf(calendar.get(Calendar.YEAR)))) {
                LOGGER.info("Transaction Id: {}, Year found: {}", MDC.get("transactionId"), year);
                List<TrainingMonth> trainingMonths = year.getMonths();
                for (TrainingMonth month : trainingMonths) {
                    if (month.getMonthName().equals(String.valueOf(calendar.get(Calendar.MONTH)))) {
                        LOGGER.info("Transaction Id: {}, Month found: {}", MDC.get("transactionId"), month);
                        int result = month.getHours() - trainingRequest.getDuration();
                        if (result == 0){
                            LOGGER.info("Transaction Id: {}, Deleting month: {}", MDC.get("transactionId"), month);
                            trainingMonths.remove(month);
                            trainingMonthRepository.delete(month);
                        } else {
                            LOGGER.info("Transaction Id: {}, Updating month: {}", MDC.get("transactionId"), month);
                            month.setHours(result);
                            trainingMonthRepository.save(month);
                        }
                        break;
                    }
                }
                if (trainingMonths.isEmpty()) {
                    LOGGER.info("Transaction Id: {}, Deleting year: {}", MDC.get("transactionId"), year);
                    trainingYears.remove(year);
                    trainingYearsRepository.delete(year);
                } else {
                    LOGGER.info("Transaction Id: {}, Updating year: {}", MDC.get("transactionId"), year);
                    year.setMonths(trainingMonths);
                    trainingYearsRepository.save(year);
                }
                break;
            }
        }
        if (trainingYears.isEmpty()) {
            LOGGER.info("Transaction Id: {}, Deleting training work: {}", MDC.get("transactionId"), trainingWork);
            trainingWorkRepository.delete(trainingWork);
        } else {
            LOGGER.info("Transaction Id: {}, Updating training work: {}", MDC.get("transactionId"), trainingWork);
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
        LOGGER.info("Transaction Id: {}, Successfully created training work: {}", MDC.get("transactionId"), trainingWork);
    }

    private void updateTrainingWork(TrainingRequest trainingRequest) {
        TrainingWork trainingWork = trainingWorkRepository.findByUsername(trainingRequest.getUsername()).get();
        List<TrainingYears> years = updateTrainingYears(trainingWork, trainingRequest);
        trainingWork.setYears(years);
        trainingWorkRepository.save(trainingWork);
        LOGGER.info("Transaction Id: {}, Successfully updated training work: {}", MDC.get("transactionId"), trainingWork);
    }

    private TrainingYears createTrainingYears(TrainingRequest trainingRequest) {
        LOGGER.info("Transaction Id: {}, Creating new training years", MDC.get("transactionId"));
        TrainingYears trainingYears = new TrainingYears();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(trainingRequest.getDate());
        trainingYears.setYearNumber(String.valueOf(calendar.get(Calendar.YEAR)));
        List<TrainingMonth> months = List.of(createTrainingMonth(trainingRequest));
        trainingYears.setMonths(months);
        trainingYearsRepository.save(trainingYears);
        LOGGER.info("Transaction Id: {}, Successfully created training years: {}", MDC.get("transactionId"), trainingYears);
        return trainingYears;
    }

    private List<TrainingYears> updateTrainingYears(TrainingWork trainingWork, TrainingRequest trainingRequest) {
        List<TrainingYears> trainingYears = trainingWork.getYears();
        boolean present = false;
        for (TrainingYears year : trainingYears) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(trainingRequest.getDate());
            if (year.getYearNumber().equals(String.valueOf(calendar.get(Calendar.YEAR)))) {
                LOGGER.info("Transaction Id: {}, Year found: {}", MDC.get("transactionId"), year);
                List<TrainingMonth> months = updateTrainingMonth(year, trainingRequest);
                year.setMonths(months);
                trainingYearsRepository.save(year);
                LOGGER.info("Transaction Id: {}, Successfully updated training years: {}", MDC.get("transactionId"), year);
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
        LOGGER.info("Transaction Id: {}, Creating new training month", MDC.get("transactionId"));
        TrainingMonth trainingMonth = new TrainingMonth();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(trainingRequest.getDate());
        trainingMonth.setMonthName(String.valueOf(calendar.get(Calendar.MONTH)));
        trainingMonth.setHours(trainingRequest.getDuration());
        trainingMonthRepository.save(trainingMonth);
        LOGGER.info("Transaction Id: {}, Successfully created training month: {}", MDC.get("transactionId"), trainingMonth);
        return trainingMonth;
    }

    private List<TrainingMonth> updateTrainingMonth(TrainingYears trainingYears, TrainingRequest trainingRequest) {
        List<TrainingMonth> trainingMonths = trainingYears.getMonths();
        boolean present = false;
        for (TrainingMonth month : trainingMonths) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(trainingRequest.getDate());
            if (month.getMonthName().equals(String.valueOf(calendar.get(Calendar.MONTH)))) {
                LOGGER.info("Transaction Id: {}, Month found: {}", MDC.get("transactionId"), month);
                int hours = month.getHours() + trainingRequest.getDuration();
                month.setHours(hours);
                trainingMonthRepository.save(month);
                LOGGER.info("Transaction Id: {}, Successfully updated training month: {}", MDC.get("transactionId"), month);
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
