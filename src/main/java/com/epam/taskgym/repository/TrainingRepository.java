package com.epam.taskgym.repository;

import com.epam.taskgym.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {
    List<Training> findAllByTrainee_User_Username(String username);
    List<Training> findAllByTrainee_User_UsernameAndDateBetween(String username, Date startDate, Date endDate);
    List<Training> findAllByTrainee_User_UsernameAndTrainer_User_FirstName(String username, String trainerName);
    List<Training> findAllByTrainee_User_UsernameAndTrainingType_name(String username, String trainingType);
    List<Training> findAllByTrainer_User_Username(String username);
    List<Training> findAllByTrainer_User_UsernameAndDateBetween(String username, Date startDate, Date endDate);
    List<Training> findAllByTrainer_User_UsernameAndTrainee_User_FirstName(String username, String traineeName);
    void deleteAllByTrainee_User_Username(String username);
}
