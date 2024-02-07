package com.epam.taskgym;

import com.epam.taskgym.dao.TraineeDAO;
import com.epam.taskgym.dao.TrainerDAO;
import com.epam.taskgym.dao.UserDAO;
import com.epam.taskgym.dto.TrainerDTO;
import com.epam.taskgym.service.TrainerService;
import com.epam.taskgym.storage.TraineeInMemoryDb;
import com.epam.taskgym.storage.TrainerInMemoryDb;
import com.epam.taskgym.storage.UserInMemoryDb;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TrainerServiceTest {

    private UserDAO userDAO;
    private TrainerDAO trainerDAO;

    private TrainerService trainerService;

    private TrainerDTO trainerDTOTest;

    @BeforeEach
    public void setup(){
        UserInMemoryDb userInMemoryDb = new UserInMemoryDb();
        userDAO = new UserDAO(userInMemoryDb);
        trainerDAO = new TrainerDAO(new TrainerInMemoryDb(), userInMemoryDb);
        TraineeDAO traineeDAO = new TraineeDAO(new TraineeInMemoryDb(), userInMemoryDb);

        trainerService = new TrainerService(userDAO, trainerDAO, traineeDAO);
        trainerDTOTest = trainerService.registerTrainer("First", "Last", "Specialization");
    }

    @Test
    public void testRegisterTrainer() {
        // Register a new trainer
        String firstName = "First";
        String lastName = "Last";
        String specialization = "Something";

        TrainerDTO trainerDTO = trainerService.registerTrainer(firstName, lastName, specialization);

        // Now you can assert that the trainerDTO object is initialized and the fields set are as expected
        assertNotNull(trainerDTO);
        assertEquals(firstName, trainerDTO.getFirstName());
        assertEquals(lastName, trainerDTO.getLastName());
        assertEquals(specialization, trainerDTO.getSpecialization());
        assertEquals("first.last1", trainerDTO.getUsername());
        assertNotNull(trainerDTO.getPassword());
        // here assert other fields as needed
    }

    @Test
    public void testGetTrainer() {
        // It should return a TraineeDTO object when username is correct
        TrainerDTO fetchedTrainer = trainerService.getTrainer(trainerDTOTest.getUsername());

        assertNotNull(fetchedTrainer);
        assertEquals(trainerDTOTest.getUsername(), fetchedTrainer.getUsername());
        assertEquals(trainerDTOTest.getFirstName(), fetchedTrainer.getFirstName());
        assertEquals(trainerDTOTest.getLastName(), fetchedTrainer.getLastName());

        // It should return null if username does not exist
        TrainerDTO nonExistentTrainer = trainerService.getTrainer("NonExistentUsername");
        assertNull(nonExistentTrainer);
    }

    @Test
    public void testUpdateTrainer() {
        Map<String, String> updates = new HashMap<>();
        updates.put("firstName", "NewFirstName");
        updates.put("lastName", "NewLastName");
        updates.put("specialization", "NewSpecialization");

        // Update trainer details
        TrainerDTO updatedTrainer = trainerService.updateTrainer(trainerDTOTest.getUsername(), updates);

        assertNotNull(updatedTrainer);
        assertNotEquals(trainerDTOTest.getFirstName(), updatedTrainer.getFirstName());
        assertNotEquals(trainerDTOTest.getLastName(), updatedTrainer.getLastName());
        assertNotEquals(trainerDTOTest.getSpecialization(), updatedTrainer.getSpecialization());
        assertEquals("NewFirstName", updatedTrainer.getFirstName());
        assertEquals("NewLastName", updatedTrainer.getLastName());
        assertEquals("NewSpecialization", updatedTrainer.getSpecialization());
    }

    @Test
    public void testDeleteTrainer() {
        // Let's first verify that the trainer does exist
        TrainerDTO existingTrainer = trainerService.getTrainer(trainerDTOTest.getUsername());
        assertNotNull(existingTrainer);

        // Delete the trainer
        trainerService.deleteTrainer(existingTrainer.getUsername());

        // Test that the trainer doesn't exist anymore
        TrainerDTO deletedTrainer = trainerService.getTrainer(existingTrainer.getUsername());
        assertNull(deletedTrainer);
    }
}
