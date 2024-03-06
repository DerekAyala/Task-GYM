package com.epam.taskgym.models;

import com.epam.taskgym.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserResponse {
    private User user;
    private String password;
}
