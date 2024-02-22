package com.epam.taskgym.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TrainingDTO {
    private String traineeUsername;
    private String trainerUsername;
    private Date date;
    private int duration;
    private String name;
}
