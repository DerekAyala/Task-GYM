package com.epam.microservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class TrainingMonth {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String monthName;
    private Integer hours;
}
