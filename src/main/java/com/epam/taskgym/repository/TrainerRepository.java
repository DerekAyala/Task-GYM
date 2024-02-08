package com.epam.taskgym.repository;

import com.epam.taskgym.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    Optional<Trainer> findByUserUsername(String username);
    Optional<Trainer> findByUserUsernameAndUserPassword(String username, String password);
}
