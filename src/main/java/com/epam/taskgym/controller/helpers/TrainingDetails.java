package com.epam.taskgym.controller.helpers;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class TrainingDetails {
    private String traineeUsername;
    private String trainerUsername;
    private Date date;
    private String trainingTypeName;
    private int duration;
    private String name;
}
