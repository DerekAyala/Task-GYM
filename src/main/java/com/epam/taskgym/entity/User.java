package com.epam.taskgym.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class User extends BaseIdEntity {

    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private boolean isActive;
}
