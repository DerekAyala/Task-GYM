package com.epam.taskgym.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;

@Getter
@AllArgsConstructor
@ToString
public class TraineeDTO {
    private String username;
    private String password;
    private String firstName;
    private String lastName;

    private Date dateOfBirth;
    private String address;
}
