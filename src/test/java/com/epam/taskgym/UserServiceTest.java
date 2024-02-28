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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
        user.setUsername("John.Smith");
        user.setPassword("password");
        user.setIsActive(false);
    }

    @Test
    void whenFirstNameAndLastNameAreNotNull_thenCreatesUser() {
        assertDoesNotThrow(() -> userService.createUser("John", "Smith"));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void whenFirstNameIsEmpty_thenThrowsMissingAttributesException() {
        Exception exception = assertThrows(MissingAttributes.class, () -> {
            userService.createUser("", "Smith");
        });

        String expectedMessage = "First name and last name are required";
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
    public void testUpdateUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);
        User updatedUser = userService.updateUser(user.getFirstName(), user.getLastName(), user);

        assertEquals(updatedUser.getFirstName(), user.getFirstName());
        assertEquals(updatedUser.getLastName(), user.getLastName());
    }

    @Test
    void whenNewPasswordIsValid_thenPasswordGetsUpdated() {
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> userService.updatePassword(user.getUsername(), user.getPassword(), "newPassword1234"));

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void whenNewPasswordIsNull_thenThrowsInvalidPasswordException() {
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(user));

        Exception exception = assertThrows(InvalidPasswordException.class, () -> {
            userService.updatePassword(user.getUsername(), user.getPassword(), null);
        });

        String expectedMessage = "Password cannot be null or empty.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        verify(userRepository, times(0)).save(any(User.class));
    }

    @Test
    void whenNewPasswordHasFewerThanEightCharacters_thenThrowsInvalidPasswordException() {
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(user));

        Exception exception = assertThrows(InvalidPasswordException.class, () -> {
            userService.updatePassword(user.getUsername(), user.getPassword(), "short");
        });

        String expectedMessage = "Password must be at least 8 characters long.";
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
    void whenUsernameAndPasswordAreValid_thenReturnUser() {
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(user));

        User authenticatedUser = userService.authenticateUser(user.getUsername(), user.getPassword());

        assertEquals(user, authenticatedUser);
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

    @Test
    void whenPasswordIsIncorrect_thenThrowsFailAuthenticateException() {
        User userWithWrongPassword = new User();
        userWithWrongPassword.setUsername(user.getUsername());
        userWithWrongPassword.setPassword("wrongPassword");

        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(user));

        Exception exception = assertThrows(FailAuthenticateException.class, () -> {
            userService.authenticateUser(userWithWrongPassword.getUsername(), userWithWrongPassword.getPassword());
        });

        String expectedMessage = "Fail to authenticate: Password and username do not match";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
