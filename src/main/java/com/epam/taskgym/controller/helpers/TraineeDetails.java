package com.epam.taskgym.controller.helpers;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class TraineeDetails {
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String address;
}
