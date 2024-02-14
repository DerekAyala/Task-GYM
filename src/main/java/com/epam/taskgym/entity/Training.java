package com.epam.taskgym.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
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
    @Column(name = "training_name", nullable=false)
    @NonNull
    private String name;
    @Column(name = "training_date", nullable=false)
    @NonNull
    @Temporal(TemporalType.DATE)
    private Date date;
    @ManyToOne
    @NonNull
    private TrainingType trainingType;
    @Column(name = "training_duration", nullable=false)
    @NonNull
    private Integer duration;
}
