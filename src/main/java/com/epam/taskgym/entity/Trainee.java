package com.epam.taskgym.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "trainee")
@NoArgsConstructor
public class Trainee {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "date_of_birth")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;
    @OneToOne
    @NonNull
    private User user;
    @Column(name = "address")
    private String address;

    @ManyToMany(mappedBy = "trainees")
    private List<Trainer> trainers;
}
