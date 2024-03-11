package com.epam.taskgym.models;

import com.epam.taskgym.entity.TrainingType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class TrainingResponse {
    private String TrainingName;
    private Date trainingDate;
    private TrainingType trainingType;
    private Integer duration;
    private String traineeOrTrainerName;
}
