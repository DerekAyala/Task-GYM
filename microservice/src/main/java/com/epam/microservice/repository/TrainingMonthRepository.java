package com.epam.microservice.repository;

import com.epam.microservice.entity.TrainingMonth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingMonthRepository extends JpaRepository<TrainingMonth, Long> {
}
