package com.epam.taskgym;

import com.epam.taskgym.entity.User;
import com.epam.taskgym.repository.UserRepository;
import com.epam.taskgym.service.UserService;
import com.epam.taskgym.exception.MissingAttributes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

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

    @Test
    void createUserTestWhenUserDetailsAreMissing() {
        Map<String, String> invalidUserDetails = new HashMap<>();

        Assertions.assertThrows(MissingAttributes.class, () -> userService.createUser(invalidUserDetails));
    }

    @Test
    void createUserTestWhenUserDetailsAreValid() {
        Map<String, String> userDetails = new HashMap<>();
        userDetails.put("firstName", "John");
        userDetails.put("lastName", "Smith");

        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createUser(userDetails);

        Assertions.assertEquals("John", createdUser.getFirstName());
        Assertions.assertEquals("Smith", createdUser.getLastName());
        Assertions.assertEquals("john.smith", createdUser.getUsername());
        Assertions.assertEquals(10, createdUser.getPassword().length());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUserTest() {
        when(userRepository.save(user)).thenReturn(user);
        Map<String, String> userDetails = new HashMap<>();
        userDetails.put("firstName", "Jane");
        userDetails.put("lastName", "Doe");

        User updatedUser = userService.updateUser(userDetails, user);

        Assertions.assertEquals("Jane", updatedUser.getFirstName());
        Assertions.assertEquals("Doe", updatedUser.getLastName());

        verify(userRepository, atLeastOnce()).save(user);
    }

    @Test
    void deleteUserTest() {
        userService.deleteUser(user);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void toggleUserActivationTest() {
        when(userRepository.save(user)).thenReturn(user);
        Assertions.assertFalse(user.getIsActive());

        User updatedUser = userService.toggleUserActivation(user);

        Assertions.assertTrue(updatedUser.getIsActive());

        verify(userRepository, times(1)).save(user);
    }
}
