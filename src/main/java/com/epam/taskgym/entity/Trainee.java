package com.epam.taskgym.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Entity
@Table(name = "trainee")
@NoArgsConstructor
public class Trainee {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "dateOfBirth")
    private String dateOfBirth;
    @OneToOne
    @Column(name = "userId")
    @NonNull
    private User userId;
    @Column(name = "address")
    private String address;
}
