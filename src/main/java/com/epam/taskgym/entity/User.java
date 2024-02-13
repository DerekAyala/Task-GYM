package com.epam.taskgym.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Entity
@Table(name = "user")
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "first_name", nullable=false)
    @NonNull
    private String firstName;
    @Column(name = "last_name", nullable=false)
    @NonNull
    private String lastName;
    @Column(name = "username", unique = true, nullable=false)
    @NonNull
    private String username;
    @Column(name = "password", nullable=false)
    @NonNull
    private String password;
    @Column(name = "is_active", nullable=false)
    @NonNull
    private Boolean isActive;
}
