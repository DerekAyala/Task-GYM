package com.epam.taskgym.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Training {

    private Long id;
    private Long traineeId;
    private Long trainerId;
    private String name;
    private Long trainingTypeId;
}
