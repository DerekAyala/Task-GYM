package com.epam.taskgym.service;

import com.epam.taskgym.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class CurrentUserContext {
    private User currentUser;
    private String userType;
}
