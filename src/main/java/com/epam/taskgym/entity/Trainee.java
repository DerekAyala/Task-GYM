package com.epam.taskgym.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Trainee {

    private Long id;
    private String dateOfBirth;
    private Long userId;
    private String address;
}
