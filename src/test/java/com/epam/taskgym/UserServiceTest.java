package com.epam.taskgym;

import com.epam.taskgym.entity.User;
import com.epam.taskgym.exception.FailAuthenticateException;
import com.epam.taskgym.exception.InvalidPasswordException;
import com.epam.taskgym.exception.MissingAttributes;
import com.epam.taskgym.repository.UserRepository;
import com.epam.taskgym.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;
    @InjectMocks
    private BCryptPasswordEncoder passwordEncoder;
    User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setFirstName("John");
        user.setLastName("Smith");
        user.setUsername("John.Smith");
        user.setPassword("password");
        user.setIsActive(false);
    }

    @Test
    void whenFirstNameIsEmpty_thenThrowsMissingAttributesException() {
        Exception exception = assertThrows(MissingAttributes.class, () -> {
            userService.createUser("", "Smith");
        });

        String expectedMessage = "First name and last name are required.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    void whenLastNameIsNull_thenThrowsMissingAttributesException() {
        Exception exception = assertThrows(MissingAttributes.class, () -> {
            userService.createUser("John", null);
        });

        String expectedMessage = "First name and last name are required";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    public void testDeleteUser() {
        doNothing().when(userRepository).delete(user);
        userService.deleteUser(user);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void whenUsernameIsNull_thenThrowsMissingAttributesException() {
        Exception exception = assertThrows(MissingAttributes.class, () -> {
            userService.authenticateUser(null, "password");
        });

        String expectedMessage = "Username and password are required";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
