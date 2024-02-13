package com.epam.taskgym.dto;

import com.epam.taskgym.entity.Trainee;
import com.epam.taskgym.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
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
