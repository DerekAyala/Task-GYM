package com.epam.taskgym.dto;

import com.epam.taskgym.entity.Trainee;
import com.epam.taskgym.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
