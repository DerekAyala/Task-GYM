package com.epam.taskgym.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainerDTO {
    private Long userId;
    private String username;
    private String password;
    private String firstName;
    private String lastName;

    private Long trainerId;
    private String specialization;
}
