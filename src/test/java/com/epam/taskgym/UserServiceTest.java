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

    @Test
    void createUserTestWhenUserDetailsAreValid() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        User createdUser = userService.createUser("John", "Smith");

        Assertions.assertEquals("John", createdUser.getFirstName());
        Assertions.assertEquals("Smith", createdUser.getLastName());
        Assertions.assertEquals("john.smith", createdUser.getUsername());
        Assertions.assertEquals(10, createdUser.getPassword().length());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUserTest() {
        when(userRepository.save(user)).thenReturn(user);
        User updatedUser = userService.updateUser("Jane", "Doe", user);

        Assertions.assertEquals("Jane", updatedUser.getFirstName());
        Assertions.assertEquals("Doe", updatedUser.getLastName());

        verify(userRepository, atLeastOnce()).save(user);
    }

    @Test
    void deleteUserTest() {
        userService.deleteUser(user);

        verify(userRepository, times(1)).delete(user);
    }
}
