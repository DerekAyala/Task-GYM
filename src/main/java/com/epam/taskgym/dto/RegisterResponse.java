package com.epam.taskgym.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@AllArgsConstructor
public class RegisterResponse {
    private String username;
    private String password;
}
