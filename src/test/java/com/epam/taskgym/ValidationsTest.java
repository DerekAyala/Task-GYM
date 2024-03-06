package com.epam.taskgym;

import com.epam.taskgym.exception.BadRequestException;
import com.epam.taskgym.exception.InvalidPasswordException;
import com.epam.taskgym.exception.MissingAttributes;
import com.epam.taskgym.helpers.Validations;
import com.epam.taskgym.models.TraineeDTO;
import com.epam.taskgym.models.TrainerDTO;
import com.epam.taskgym.models.TrainingDTO;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ValidationsTest {

    @Test
    public void givenValidUserDetails_whenValidateUserDetails_thanNoException() {
        assertDoesNotThrow(() -> Validations.validateUserDetails("John", "Doe"));
    }

    @Test
    public void givenNullAttribute_whenValidateUserDetails_thanException() {
        Exception exception = assertThrows(MissingAttributes.class, () -> Validations.validateUserDetails(null, "Doe"));
        String expectedMessage = "First name and last name are required.";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void givenEmptyAttribute_whenValidateUserDetails_thanException() {
        Exception exception = assertThrows(MissingAttributes.class, () -> Validations.validateUserDetails("", "Doe"));
        String expectedMessage = "First name and last name are required.";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void givenValidTraineeDetails_whenValidateTraineeDetails_thanNoException() {
        TraineeDTO traineeDTO = new TraineeDTO();
        assertDoesNotThrow(() -> Validations.validateTraineeDetails(traineeDTO));
    }

    @Test
    public void givenNullTraineeDetails_whenValidateTraineeDetails_thanException() {
        Exception exception = assertThrows(MissingAttributes.class, () -> Validations.validateTraineeDetails(null));
        String expectedMessage = "Trainee details cannot be null";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void givenValidTrainerDetails_whenValidateTraineeDetails_thanNoException() {
        TrainerDTO trainerDTO = new TrainerDTO();
        assertDoesNotThrow(() -> Validations.validateTrainerDetails(trainerDTO));
    }

    @Test
    public void givenNullTrainerDetails_whenValidateTraineeDetails_thanException() {
        Exception exception = assertThrows(MissingAttributes.class, () -> Validations.validateTrainerDetails(null));
        String expectedMessage = "Trainer details cannot be null or empty";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void givenValidName_whenValidateTrainingTypeDetails_thanNoException() {
        assertDoesNotThrow(() -> Validations.validateTrainingTypeDetails("ValidName"));
    }

    @Test
    public void givenNullName_whenValidateTrainingTypeDetails_thanException() {
        Exception exception = assertThrows(MissingAttributes.class, () -> Validations.validateTrainingTypeDetails(null));
        String expectedMessage = "Name is required";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void givenEmptyName_whenValidateTrainingTypeDetails_thanException() {
        Exception exception = assertThrows(MissingAttributes.class, () -> Validations.validateTrainingTypeDetails(""));
        String expectedMessage = "Name is required";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void givenValidTrainingDetails_whenValidateTrainingDetails_thanNoException() {
        TrainingDTO trainingDTO = new TrainingDTO();
        trainingDTO.setTraineeUsername("Trainee");
        trainingDTO.setTrainerUsername("Trainer");
        trainingDTO.setDate(new Date());
        trainingDTO.setName("ValidName");
        trainingDTO.setDuration(1);
        assertDoesNotThrow(() -> Validations.validateTrainingDetails(trainingDTO));
    }

    @Test
    public void givenNullTrainingDetails_whenValidateTrainingDetails_thanException() {
        Exception exception = assertThrows(MissingAttributes.class,
                () -> Validations.validateTrainingDetails(null));
        String expectedMessage = "Training details are required";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void givenEmptyTraineeUsername_whenValidateTrainingDetails_thanException() {
        TrainingDTO trainingDTO = new TrainingDTO();
        trainingDTO.setTraineeUsername("");
        trainingDTO.setTrainerUsername("Trainer");
        trainingDTO.setDate(new Date());
        trainingDTO.setName("ValidName");
        trainingDTO.setDuration(1);
        Exception exception = assertThrows(MissingAttributes.class,
                () -> Validations.validateTrainingDetails(trainingDTO));
        String expectedMessage = "Trainee username, trainer username, date, training type name, name and duration are required";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void givenValidSpecialization_whenValidateSpecialization_thanNoException() {
        assertDoesNotThrow(() -> Validations.validateSpecialization("Specialization"));
    }

    @Test
    public void givenNullSpecialization_whenValidateSpecialization_thanException() {
        Exception exception = assertThrows(MissingAttributes.class, () -> Validations.validateSpecialization(null));
        String expectedMessage = "specialization is required";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void givenEmptySpecialization_whenValidateSpecialization_thanException() {
        Exception exception = assertThrows(MissingAttributes.class, () -> Validations.validateSpecialization(""));
        String expectedMessage = "specialization is required";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void givenValidDate_whenValidateDate_thanNoExceptionAndCorrectDate() throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        Date expectedDate = df.parse("31-12-2020");
        Date actualDate = Validations.validateDate("31-12-2020");
        assertEquals(actualDate, expectedDate);
    }

    @Test
    public void givenInvalidDate_whenValidateDate_thanException() {
        Exception exception = assertThrows(BadRequestException.class, () -> Validations.validateDate("31/12/2020"));
        String expectedMessage = "Invalid date format {DD-MM-YYYY}";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void givenValidList_whenValidateList_thanNoException() {
        List<String> list = Arrays.asList("item1", "item2");
        assertDoesNotThrow(() -> Validations.validateList(list));
    }

    @Test
    public void givenNullList_whenValidateList_thanException() {
        Exception exception = assertThrows(MissingAttributes.class, () -> Validations.validateList(null));
        String expectedMessage = "List cannot be null or empty";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void givenEmptyList_whenValidateList_thanException() {
        List<String> emptyList = new ArrayList<>();
        Exception exception = assertThrows(MissingAttributes.class, () -> Validations.validateList(emptyList));
        String expectedMessage = "List cannot be null or empty";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void givenValidUsername_whenValidateUsername_thanNoException() {
        assertDoesNotThrow(() -> Validations.validateUsername("ValidUsername"));
    }

    @Test
    public void givenNullUsername_whenValidateUsername_thanException() {
        Exception exception = assertThrows(MissingAttributes.class, () -> Validations.validateUsername(null));
        String expectedMessage = "Username is required";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void givenEmptyUsername_whenValidateUsername_thanException() {
        Exception exception = assertThrows(MissingAttributes.class, () -> Validations.validateUsername(""));
        String expectedMessage = "Username is required";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void givenValidDuration_whenValidateDuration_thanNoExceptionAndCorrectInteger() {
        int actualInt = Validations.validateDuration("5");
        assertEquals(actualInt, 5);
    }

    @Test
    public void givenNonNumericDuration_whenValidateDuration_thanException() {
        Exception exception = assertThrows(BadRequestException.class,
                () -> Validations.validateDuration("NonNumericInput"));
        String expectedMessage = "Duration must be a number";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void givenNullDuration_whenValidateDuration_thanException() {
        Exception exception = assertThrows(BadRequestException.class,
                () -> Validations.validateDuration(null));
        String expectedMessage = "Duration must be a number";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void givenValidPassword_whenValidatePassword_thanNoException() {
        assertDoesNotThrow(() -> Validations.validatePassword("ValidPassword123"));
    }

    @Test
    public void givenNullPassword_whenValidatePassword_thanException() {
        Exception exception = assertThrows(InvalidPasswordException.class, () -> Validations.validatePassword(null));
        String expectedMessage = "Password cannot be null or empty.";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void givenEmptyPassword_whenValidatePassword_thanException() {
        Exception exception = assertThrows(InvalidPasswordException.class, () -> Validations.validatePassword(""));
        String expectedMessage = "Password cannot be null or empty.";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void givenShortPassword_whenValidatePassword_thanException() {
        Exception exception = assertThrows(InvalidPasswordException.class, () -> Validations.validatePassword("short"));
        String expectedMessage = "Password must be at least 8 characters long.";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

}
