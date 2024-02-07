package com.epam.taskgym.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class Training extends BaseIdEntity {

    private Long traineeId;
    private Long trainerId;
    private String name;
    private Long trainingTypeId;
}
