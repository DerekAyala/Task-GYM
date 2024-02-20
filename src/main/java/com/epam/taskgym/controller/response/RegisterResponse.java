package com.epam.taskgym.controller.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class RegisterResponse {
    private String username;
    private String password;

    public RegisterResponse(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
