package com.epam.taskgym.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class TrainingFilteredDTO {
    private String username;
    private Date dateFrom;
    private Date dateTo;
    private String trainingTypeName;
    private String TrainerOrTraineeName;

}
