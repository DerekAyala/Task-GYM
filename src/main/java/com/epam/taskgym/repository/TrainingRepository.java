package com.epam.taskgym.repository;

import com.epam.taskgym.entity.Trainee;
import com.epam.taskgym.entity.Training;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface TrainingRepository extends JpaRepository<Training, Long> {
    List<Training> findAllByTrainee_User_Username(String username);
    List<Training> findAllByTrainee_User_UsernameOrderByTrainer_User_firstName(String username);
    List<Training> findAllByTrainee_User_UsernameAndTrainingType_name(@NonNull String username, @NonNull String trainingType);
    List<Training> findAllByTrainer_User_Username(String username);
    List<Training> findAllByTrainer_User_UsernameOrderByTrainee_User_firstNameAsc(String username);
}
