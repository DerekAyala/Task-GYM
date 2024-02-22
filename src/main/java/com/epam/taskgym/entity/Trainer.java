package com.epam.taskgym.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
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

    @Override
    public String toString() {
        return "Trainer{" +
                "First Name='" + user.getFirstName() +
                ", Last Name='" + user.getLastName() +
                ", Specialization=" + specialization +
                ", Is Active=" + user.getIsActive() +
                '}';
    }
}
