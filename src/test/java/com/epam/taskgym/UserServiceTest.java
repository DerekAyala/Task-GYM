package com.epam.taskgym;

import com.epam.taskgym.entity.User;
import com.epam.taskgym.repository.UserRepository;
import com.epam.taskgym.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;
    User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setFirstName("John");
        user.setLastName("Smith");
        user.setIsActive(false);
    }
}
