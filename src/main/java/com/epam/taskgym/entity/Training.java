package com.epam.taskgym.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Entity
@Table(name = "training")
@NoArgsConstructor
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Column(name = "traineeId")
    @NonNull
    private Trainee traineeId;
    @ManyToOne
    @Column(name = "trainerId")
    @NonNull
    private Trainer trainerId;
    @Column(name = "name", nullable=false)
    @NonNull
    private String name;
    @Column(name = "Date", nullable=false)
    @NonNull
    private String date;
    @ManyToOne
    @Column(name = "trainingTypeId", nullable=false)
    @NonNull
    private TrainingType trainingTypeId;
}
