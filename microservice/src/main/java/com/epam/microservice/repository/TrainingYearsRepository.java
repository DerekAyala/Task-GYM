package com.epam.microservice.repository;

import com.epam.microservice.entity.TrainingYears;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingYearsRepository extends JpaRepository<TrainingYears, Long> {
}
