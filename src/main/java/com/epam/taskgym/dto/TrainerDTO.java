package com.epam.taskgym.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TrainerDTO {
    private Long userId;
    private String username;
    private String password;
    private String firstName;
    private String lastName;

    private Long trainerId;
    private String specialization;
}
