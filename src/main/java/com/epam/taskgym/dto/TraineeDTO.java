package com.epam.taskgym.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class TraineeDTO {
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String address;
}
