package com.epam.taskgym.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Table(name = "trainer")
@NoArgsConstructor
public class Trainer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @NonNull
    private TrainingType specialization;
    @OneToOne
    @NonNull
    private User user;

    @ManyToMany
    private List<Trainee> trainees;
}
