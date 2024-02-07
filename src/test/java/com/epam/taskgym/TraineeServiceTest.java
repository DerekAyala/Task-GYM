package com.epam.taskgym;

import com.epam.taskgym.dao.TraineeDAO;
import com.epam.taskgym.dao.UserDAO;
import com.epam.taskgym.dto.TraineeDTO;
import com.epam.taskgym.service.TraineeService;
import com.epam.taskgym.storage.TraineeInMemoryDb;
import com.epam.taskgym.storage.UserInMemoryDb;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TraineeServiceTest {

    // Actual instances, not mocks
    private UserDAO userDAO;
    private TraineeDAO traineeDAO;

    private TraineeService traineeService;

    private TraineeDTO traineeDTOTest;

    @BeforeEach
    public void setup(){
        // Reset the repositories before each test
        UserInMemoryDb userInMemoryDb = new UserInMemoryDb();
        userDAO = new UserDAO(userInMemoryDb);
        traineeDAO = new TraineeDAO(new TraineeInMemoryDb(), userInMemoryDb);

        traineeService = new TraineeService(userDAO, traineeDAO);
        traineeDTOTest = traineeService.registerTrainee("First", "Last", "2000-07-10", "123 Trainee Street");
    }

    @Test
    public void testRegisterTrainee() {
        String firstName = "First";
        String lastName = "Last";
        String dateOfBirth = "2000-07-10";
        String address = "123 Trainee Street";

        TraineeDTO traineeDTO = traineeService.registerTrainee(firstName, lastName, dateOfBirth, address);

        // Assertions
        assertNotNull(traineeDTO);
        assertEquals(firstName, traineeDTO.getFirstName());
        assertEquals(lastName, traineeDTO.getLastName());
        assertEquals(dateOfBirth, traineeDTO.getDateOfBirth());
        assertEquals(address, traineeDTO.getAddress());
        // test unique username
        assertEquals("first.last1", traineeDTO.getUsername());
        assertNotNull(traineeDTO.getPassword());
    }

    @Test
    public void testAuthenticateTrainee() {
        // it should return true when credentials are correct
        boolean isAuthenticated = traineeService.authenticateTrainee(traineeDTOTest.getUsername(), traineeDTOTest.getPassword());
        assertTrue(isAuthenticated);

        // it should return false when password is incorrect
        isAuthenticated = traineeService.authenticateTrainee(traineeDTOTest.getUsername(), "wrongPassword");
        assertFalse(isAuthenticated);
    }

    @Test
    public void testGetTrainee() {
        // It should return a TraineeDTO object when username is correct
        TraineeDTO traineeDTO = traineeService.getTrainee(traineeDTOTest.getUsername());
        assertNotNull(traineeDTO);
        assertEquals(traineeDTOTest.getUsername(), traineeDTO.getUsername());

        // It should return null when username does not exist
        traineeDTO = traineeService.getTrainee("NonExistingUsername");
        assertNull(traineeDTO);
    }

    @Test
    public void testUpdateTrainee() {
        // Update firstName and address
        Map<String, String> updates = new HashMap<>();
        updates.put("firstName", "UpdatedFirstName");
        updates.put("lastName", "UpdatedLastName");
        updates.put("dateOfBirth", "2000-07-11");
        updates.put("address", "Updated address 123");

        TraineeDTO updatedTraineeDTO = traineeService.updateTrainee(traineeDTOTest.getUsername(), updates);

        // Assert updated values
        assertNotNull(updatedTraineeDTO);
        assertEquals(updatedTraineeDTO.getFirstName(), "UpdatedFirstName");
        assertEquals(updatedTraineeDTO.getAddress(), "Updated address 123");

        // Assert old values are unchanged
        assertEquals(updatedTraineeDTO.getDateOfBirth(), traineeDTOTest.getDateOfBirth());
        assertEquals(updatedTraineeDTO.getLastName(), traineeDTOTest.getLastName());
    }

    @Test
    public void testDeleteTrainee() {
        // At first, trainee should exist
        TraineeDTO existingTraineeDTO = traineeService.getTrainee(traineeDTOTest.getUsername());
        assertNotNull(existingTraineeDTO);

        // Delete user
        traineeService.deleteTrainee(traineeDTOTest.getUsername());

        // Now, trainee should not exist
        TraineeDTO deletedTraineeDTO = traineeService.getTrainee(traineeDTOTest.getUsername());
        assertNull(deletedTraineeDTO);
    }

    // Similar tests for other operations...
}