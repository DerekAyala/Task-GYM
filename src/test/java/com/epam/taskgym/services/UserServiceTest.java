package com.epam.taskgym.services;

import com.epam.taskgym.entity.User;
import com.epam.taskgym.exception.MissingAttributes;
import com.epam.taskgym.exception.NotFoundException;
import com.epam.taskgym.models.UserResponse;
import com.epam.taskgym.repository.UserRepository;
import com.epam.taskgym.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    public void givenValidUser_whenCreateUser_thenCreatedUserReturned() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("john.doe");
        user.setIsActive(true);
        given(userRepository.save(any(User.class))).willReturn(user);
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");

        UserResponse userResponse = userService.createUser("John", "Doe", "role");

        assertEquals(user.getUsername(), userResponse.getUser().getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void givenInvalidUser_whenCreateUser_thenExceptionThrown() {
        Exception exception = assertThrows(MissingAttributes.class, () -> userService.createUser(null, "Doe", "role"));

        assertEquals("First name and last name are required.", exception.getMessage());
    }

    @Test
    public void givenValidDetails_whenUpdateUser_thenUserUpdated() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setFirstName("John");
        existingUser.setLastName("Doe");
        existingUser.setUsername("J.Doe");
        given(userRepository.save(any(User.class))).willReturn(existingUser);

        User updatedUser = userService.updateUser("John", "Smith", existingUser);

        assertEquals("John", updatedUser.getFirstName());
        assertEquals("Smith", updatedUser.getLastName());
        verify(userRepository).save(existingUser);
    }

    @Test
    public void givenEmptyDetails_whenUpdateUser_thenUserNotUpdated() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setFirstName("John");
        existingUser.setLastName("Doe");
        existingUser.setUsername("J.Doe");
        given(userRepository.save(any(User.class))).willReturn(existingUser);

        User updatedUser = userService.updateUser("", "", existingUser);

        assertEquals("John", updatedUser.getFirstName());
        assertEquals("Doe", updatedUser.getLastName());
        verify(userRepository).save(existingUser);
    }

    @Test
    public void givenValidUser_whenDeleteUser_thenUserDeleted() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("J.Doe");

        userService.deleteUser(user);

        verify(userRepository).delete(user);
    }

    @Test
    public void givenValidDetails_whenUpdatePassword_thenPasswordUpdated() {
        // given
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("J.Doe");
        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(user));
        given(userRepository.save(any(User.class))).willReturn(user);
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");

        // when
        User updatedUser = userService.updatePassword("J.Doe", "newPassword");

        // then
        assertEquals("encodedPassword", updatedUser.getPassword());
        verify(userRepository).findByUsername("J.Doe");
        verify(userRepository).save(updatedUser);
    }

    @Test
    public void givenInvalidUser_whenUpdatePassword_thenExceptionThrown() {
        // given
        given(userRepository.findByUsername("invalidUser")).willReturn(Optional.empty());

        // when
        Exception exception = assertThrows(NotFoundException.class,
                () -> userService.updatePassword("invalidUser", "newPassword"));

        // then
        assertEquals("User not found", exception.getMessage());
    }
}
