package com.epam.taskgym.repository;

import com.epam.taskgym.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    Optional<Trainer> findByUserUsername(String username);
    List<Trainer> findAll();

    List<Trainer> findByUserUsername(List<String> trainerUsernames);
}
