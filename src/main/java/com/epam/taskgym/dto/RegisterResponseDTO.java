package com.epam.taskgym.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@AllArgsConstructor
public class RegisterResponseDTO {
    private String username;
    private String password;
}
