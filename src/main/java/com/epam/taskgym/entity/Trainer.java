package com.epam.taskgym.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Trainer {

    private Long id;
    private String specialization;
    private Long userId;
}
