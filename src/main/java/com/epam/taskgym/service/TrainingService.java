package com.epam.taskgym.service;

import com.epam.taskgym.models.TrainingDTO;
import com.epam.taskgym.models.TrainingFilteredDTO;
import com.epam.taskgym.models.TrainingRequest;
import com.epam.taskgym.models.TrainingResponse;
import com.epam.taskgym.entity.Trainee;
import com.epam.taskgym.entity.Trainer;
import com.epam.taskgym.entity.Training;
import com.epam.taskgym.helpers.Builders;
import com.epam.taskgym.helpers.Validations;
import com.epam.taskgym.repository.TrainingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingService {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingRepository trainingRepository;
    private final JmsTemplate jmsTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingService.class);

    @Transactional
    public TrainingDTO createTraining(TrainingDTO trainingDTO) {
        Validations.validateTrainingDetails(trainingDTO);
        LOGGER.info("Training Id: {}, Method: {}, Creating training: {}", MDC.get("transactionId"), MDC.get("MethodName"), trainingDTO);
        Trainee trainee = traineeService.getTraineeByUsername(trainingDTO.getTraineeUsername());
        Trainer trainer = trainerService.getTrainerByUsername(trainingDTO.getTrainerUsername());
        manyToManyTrainerAndTrainee(trainee, trainer);
        Training training = Builders.buildTraining(trainee, trainer, trainingDTO.getDate(), trainer.getSpecialization(), trainingDTO.getDuration(), trainingDTO.getName());
        trainingRepository.save(training);
        TrainingRequest trainingRequest = TrainingRequest.builder()
                .username(trainer.getUser().getUsername())
                .firstName(trainer.getUser().getFirstName())
                .lastName(trainer.getUser().getLastName())
                .isActive(trainer.getUser().getIsActive())
                .date(training.getDate())
                .duration(training.getDuration())
                .action("ADD")
                .transactionId(MDC.get("transactionId"))
                .build();
        jmsTemplate.convertAndSend("training", trainingRequest);
        //microserviceClient.actionTraining(trainingRequest, MDC.get("transactionId"), MDC.get("Authorization"));
        TrainingDTO trainingDTOResponse = new TrainingDTO(trainee.getUser().getUsername(), trainer.getUser().getUsername(), training.getDate(), training.getDuration(), training.getName());
        LOGGER.info("Transaction Id: {}, Successfully created training: {}", MDC.get("transactionId"), trainingDTOResponse);
        return trainingDTOResponse;
    }

    @Transactional
    public void manyToManyTrainerAndTrainee(Trainee trainee, Trainer trainer) {
        LOGGER.info("Transaction Id: {}, Method: {}, Creating many to many relationship between trainee: {} and trainer: {}", MDC.get("transactionId"), MDC.get("MethodName"), trainee.getUser().getUsername(), trainer.getUser().getUsername());
        List<Trainer> trainers = trainee.getTrainers();
        List<Trainee> trainees = trainer.getTrainees();
        if (!trainers.contains(trainer)) {
            trainers.add(trainer);
            trainerService.saveTrainer(trainer);
            LOGGER.info("Transaction Id: {}, Saving trainer: {}", MDC.get("transactionId"), trainer.getUser().getUsername());
        }
        if (!trainees.contains(trainee)) {
            traineeService.saveTrainee(trainee);
            LOGGER.info("Transaction Id: {}, Saving trainee: {}", MDC.get("transactionId"), trainee.getUser().getUsername());
            trainees.add(trainee);
        }
    }

    public List<TrainingResponse> getTraineeTrainingsFiltered(TrainingFilteredDTO trainingFilteredDTO) {
        Validations.validateUsername(trainingFilteredDTO.getUsername());
        LOGGER.info("Transaction Id: {}, Method: {}, Getting trainee trainings filtered by: {}", MDC.get("transactionId"), MDC.get("MethodName"), trainingFilteredDTO);
        List<Training> trainings = trainingRepository.getTraineeFilteredTrainings(trainingFilteredDTO.getUsername(), trainingFilteredDTO.getDateFrom(), trainingFilteredDTO.getDateTo(), trainingFilteredDTO.getTrainingTypeName(), trainingFilteredDTO.getTrainerOrTraineeName());
        return Builders.convertTrainingsToTrainingResponse(trainings, true);

    }

    public List<TrainingResponse> getTrainerTrainingsFiltered(TrainingFilteredDTO trainingFilteredDTO) {
        Validations.validateUsername(trainingFilteredDTO.getUsername());
        LOGGER.info("Transaction Id: {}, Method: {}, Getting trainer trainings filtered by: {}", MDC.get("transactionId"), MDC.get("MethodName"), trainingFilteredDTO);
        List<Training> trainings = trainingRepository.getTrainerFilteredTrainings(trainingFilteredDTO.getUsername(), trainingFilteredDTO.getDateFrom(), trainingFilteredDTO.getDateTo(), trainingFilteredDTO.getTrainerOrTraineeName());
        return Builders.convertTrainingsToTrainingResponse(trainings, false);
    }
}
