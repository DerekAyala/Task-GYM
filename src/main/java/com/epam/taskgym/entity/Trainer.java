package com.epam.taskgym.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Trainer extends BaseIdEntity {

    private String specialization;
    private Long userId;
}
