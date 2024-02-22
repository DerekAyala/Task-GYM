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

    public TrainingDTO() {
    }

    public TrainingDTO(String traineeUsername, String trainerUsername, Date date, int duration, String name) {
        this.traineeUsername = traineeUsername;
        this.trainerUsername = trainerUsername;
        this.date = date;
        this.duration = duration;
        this.name = name;
    }
}
