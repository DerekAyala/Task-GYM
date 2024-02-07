package com.epam.taskgym.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Trainee extends BaseIdEntity {

    private String dateOfBirth;
    private Long userId;
    private String address;
}
