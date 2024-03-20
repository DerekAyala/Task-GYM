package com.epam.taskgym.models;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainingRequest {
    private String username;
    private String firstName;
    private String lastName;
    private Boolean isActive;
    private Date date;
    private Integer duration;
    private String action;
    private String transactionId;
}
