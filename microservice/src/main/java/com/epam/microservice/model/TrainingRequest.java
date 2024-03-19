package com.epam.microservice.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class TrainingRequest {
    private String username;
    private String firstName;
    private String lastName;
    private Boolean isActive;
    private Date date;
    private Integer duration;
    private String action;
}
