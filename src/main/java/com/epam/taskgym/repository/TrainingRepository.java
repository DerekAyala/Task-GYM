package com.epam.taskgym.repository;

import com.epam.taskgym.entity.Trainer;
import com.epam.taskgym.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {
    Optional<Training> findByTrainee_User_UsernameAndTrainer_User_UsernameAndDateAndTrainingType_Name(String traineeUsername, String trainerUsername, Date date, String trainingTypeName);
    void deleteAllByTrainee_User_Username(String username);

    @Query("SELECT DISTINCT t.trainer FROM Training t WHERE t.trainee.user.username = :username")
    List<Trainer> findAllTrainersByTraineeUsername(@Param("username") String username);

}
