package com.epam.taskgym.dto;

import com.epam.taskgym.entity.TrainingType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class TrainerDTO {
    private String username;
    private String password;
    private String firstName;
    private String lastName;

    private TrainingType specialization;
}
