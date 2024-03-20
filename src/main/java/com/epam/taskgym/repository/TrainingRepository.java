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
    Optional<Training> findByTrainee_User_UsernameAndTrainer_User_UsernameAndDate(String traineeUsername, String trainerUsername, Date date);

    List<Training> findAllByTrainee_User_Username(String username);

    @Query("SELECT DISTINCT t.trainer FROM Training t WHERE t.trainee.user.username = :username")
    List<Trainer> findAllTrainersByTraineeUsername(@Param("username") String username);

    @Query("SELECT t FROM Training t " +
            "JOIN t.trainee trn " +
            "JOIN trn.user trnUser " +
            "JOIN t.trainer tr " +
            "JOIN tr.user trUser " +
            "WHERE " +
            "(trnUser.username = :username) AND " +
            "(:fromDate is null or t.date >= :fromDate) AND " +
            "(:toDate is null or t.date <= :toDate) AND " +
            "(:trainerName is null or trUser.firstName = :trainerName) AND " +
            "(:trainingTypeName is null or t.trainingType.name = :trainingTypeName)")
    List<Training> getTraineeFilteredTrainings(
            @Param("username") String username,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("trainerName") String trainerName,
            @Param("trainingTypeName") String trainingTypeName);

    @Query("SELECT t FROM Training t " +
            "JOIN t.trainer tr " +
            "JOIN tr.user trUser " +
            "JOIN t.trainee trn " +
            "JOIN trn.user trnUser " +
            "WHERE " +
            "(trUser.username = :username) AND " +
            "(:fromDate is null or t.date >= :fromDate) AND " +
            "(:toDate is null or t.date <= :toDate) AND " +
            "(:traineeName is null or trnUser.firstName = :traineeName)")
    List<Training> getTrainerFilteredTrainings(
            @Param("username") String username,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("traineeName") String traineeName);
}
