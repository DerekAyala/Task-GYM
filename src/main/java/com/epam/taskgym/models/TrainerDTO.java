package com.epam.taskgym.models;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class TrainerDTO {
    private String firstName;
    private String lastName;
    private String specialization;
    private boolean isActive;
    private List<TraineeListItem> trainees;
}
