package com.epam.taskgym.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
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

    @ManyToMany
    private List<Trainer> trainers;

    @Override
    public String toString() {
        return "Trainee{" +
                "First Name='" + user.getFirstName() +
                ", Last Name='" + user.getLastName() +
                ", Date of Birth=" + dateOfBirth +
                ", Address='" + address + '\'' +
                ", Is Active=" + user.getIsActive() +
                '}';
    }
}
