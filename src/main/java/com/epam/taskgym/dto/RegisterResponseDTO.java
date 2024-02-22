package com.epam.taskgym.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class RegisterResponseDTO {
    private String username;
    private String password;

    public RegisterResponseDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
