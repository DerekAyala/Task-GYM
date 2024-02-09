package com.epam.taskgym.dto;

import com.epam.taskgym.entity.Trainee;
import com.epam.taskgym.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class TraineeDTO {
    private User user;
    private String username;
    private String password;
    private String firstName;
    private String lastName;

    private Trainee trainee;
    private String dateOfBirth;
    private String address;
}
