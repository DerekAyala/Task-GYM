package com.epam.microservice.repository;

import com.epam.microservice.entity.TrainingWork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingWorkRepository extends JpaRepository<Long, TrainingWork> {
}
