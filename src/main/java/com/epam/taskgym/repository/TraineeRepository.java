package com.epam.taskgym.repository;

import com.epam.taskgym.entity.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TraineeRepository extends JpaRepository<Trainee, Long> {
    Optional<Trainee> findByUserUsername(String username);
    Optional<Trainee> findByUserUsernameAndUserPassword(String username, String password);
}
