package com.epam.taskgym.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Date;

@Data
@Entity
@Table(name = "training")
@NoArgsConstructor
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @NonNull
    private Trainee trainee;
    @ManyToOne
    @NonNull
    private Trainer trainer;
    @Column(name = "name", nullable=false)
    @NonNull
    private String name;
    @Column(name = "Date", nullable=false)
    @NonNull
    @Temporal(TemporalType.DATE)
    private Date date;
    @ManyToOne
    @NonNull
    private TrainingType trainingType;
    @Column(name = "duration", nullable=false)
    @NonNull
    private Integer duration;
}
