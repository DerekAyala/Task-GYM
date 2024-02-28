package com.epam.taskgym.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class TraineeListItem {
    private String username;
    private String firstName;
    private String lastName;
}
