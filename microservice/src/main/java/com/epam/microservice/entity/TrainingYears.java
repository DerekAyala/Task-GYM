package com.epam.microservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class TrainingYears {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String year;
    @OneToMany
    private List<TrainingMonth> trainingSumary;
}
