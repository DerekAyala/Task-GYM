package com.epam.taskgym.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Entity
@Table(name = "trainer")
@NoArgsConstructor
public class Trainer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "specialization")
    @ManyToOne
    @NonNull
    private TrainingType specialization;
    @OneToOne
    @Column(name = "userId")
    @NonNull
    private User userId;
}
