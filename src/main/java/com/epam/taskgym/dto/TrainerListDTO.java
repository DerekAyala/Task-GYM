package com.epam.taskgym.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TrainerListDTO {
    private String username;
    private String FirstName;
    private String LastName;
    private String specialization;
}
