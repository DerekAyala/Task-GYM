package com.epam.taskgym.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TraineeDTO {
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String address;
}
