package com.epam.taskgym.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class TrainingDTO {
    private String traineeUsername;
    private String trainerUsername;
    private Date date;
    private int duration;
    private String name;
}
