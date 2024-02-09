package com.epam.taskgym.dto;

import com.epam.taskgym.entity.Trainer;
import com.epam.taskgym.entity.TrainingType;
import com.epam.taskgym.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class TrainerDTO {
    private User user;
    private String username;
    private String password;
    private String firstName;
    private String lastName;

    private Trainer trainer;
    private TrainingType specialization;
}
