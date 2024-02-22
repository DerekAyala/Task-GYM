package com.epam.taskgym.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class TrainerListItem {
    private String username;
    private String FirstName;
    private String LastName;
    private String specialization;
}
