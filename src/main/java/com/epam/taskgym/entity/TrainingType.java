package com.epam.taskgym.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "training_type")
@NoArgsConstructor
public class TrainingType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "training_type_name", nullable=false)
    @NonNull
    private String name;
}
