package com.epam.taskgym.integration;

import com.epam.taskgym.models.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Assert;

import java.util.Date;
import java.util.Optional;


public class GymManagementStepDefinitions {

    private final String baseUrl = "http://localhost:8080/api";
    private Response response;
    private Object requestPayload;
    private String username;

    @Given("valid trainee details")
    public void validTraineeDetails() {
        TraineeDTO traineeDTO = new TraineeDTO();
        traineeDTO.setFirstName("Jane");
        traineeDTO.setLastName("Doe");
        // Set the trainee DTO attributes
        requestPayload = traineeDTO;
    }

    @Given("valid trainer details")
    public void validTrainerDetails() {
        TrainerDTO trainerDTO = new TrainerDTO();
        trainerDTO.setFirstName("John");
        trainerDTO.setLastName("Doe");
        trainerDTO.setSpecialization("Functional Training");
        // Set the trainer DTO attributes
        requestPayload = trainerDTO;
    }

    @Given("valid credentials")
    public void validCredentials() {
        LoginRequest userCredentialsDTO = new LoginRequest("username", "password");
        // Set the userCredentialsDTO attributes
        requestPayload = userCredentialsDTO;
    }

    @Given("valid username {string} and a new password {string}")
    public void validUsernameAndANewPassword(String username, String newPassword) {
        this.username = username;
        requestPayload = newPassword;
    }

    @Given("valid trainee username {string}")
    public void validTraineeUsername(String username) {
        this.username = username;
    }

    @Given("valid trainee username {string} and updated trainee details")
    public void validTraineeUsernameAndUpdatedTraineeDetails(String username) {
        this.username = username;
        TraineeDTO updatedTraineeDTO = new TraineeDTO();
        updatedTraineeDTO.setLastName("Smith");
        // Set the updatedTraineeDTO attributes
        requestPayload = updatedTraineeDTO;
    }

    @Given("valid trainer username {string}")
    public void validTrainerUsername(String username) {
        this.username = username;
    }

    @Given("valid trainer username {string} and updated trainer details")
    public void validTrainerUsernameAndUpdatedTrainerDetails(String username) {
        this.username = username;
        TrainerDTO updatedTrainerDTO = new TrainerDTO();
        updatedTrainerDTO.setLastName("Smith");
        // Set the updatedTrainerDTO attributes
        requestPayload = updatedTrainerDTO;
    }

    @Given("valid trainee username {string} and filter criteria")
    public void validTraineeUsernameAndFilterCriteria(String username) {
        this.username = username;
        TrainingFilteredDTO filterCriteriaDTO = new TrainingFilteredDTO(); // Replace with actual Filter Criteria DTO class
        filterCriteriaDTO.setUsername(username);
        // Set the filterCriteriaDTO attributes
        requestPayload = filterCriteriaDTO;
    }

    @Given("valid trainer username {string} and filter criteria")
    public void validTrainerUsernameAndFilterCriteria(String username) {
        this.username = username;
        TrainingFilteredDTO filterCriteriaDTO = new TrainingFilteredDTO(); // Replace with actual Filter Criteria DTO class
        filterCriteriaDTO.setUsername(username);
        // Set the filterCriteriaDTO attributes
        requestPayload = filterCriteriaDTO;
    }

    @Given("valid training details")
    public void validTrainingDetails() {
        TrainingDTO trainingDTO = new TrainingDTO(); // Replace with actual Training DTO class
        trainingDTO.setName("Functional Training");
        trainingDTO.setDate(new Date());
        trainingDTO.setDuration(60);
        trainingDTO.setTraineeUsername("traineeUsername");
        trainingDTO.setTrainerUsername("trainerUsername");
        // Set the trainingDTO attributes
        requestPayload = trainingDTO;
    }

    @Given("valid request")
    public void validRequest() {
        // No request payload needed for this scenario
    }

    @When("I send a GET request to {string}")
    public void iSendAGetRequestTo(String endpoint) {
        if (requestPayload != null) {
            response = RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(requestPayload)
                    .get(baseUrl + endpoint.replace("{username}", username));
        } else {
            response = RestAssured.get(baseUrl + endpoint.replace("{username}", username));
        }
    }

    @When("I send a POST request to {string}")
    public void iSendAPostRequestTo(String endpoint) {
        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestPayload)
                .post(baseUrl + endpoint);
    }

    @When("I send a PUT request to {string}")
    public void iSendAPutRequestTo(String endpoint) {
        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestPayload)
                .put(baseUrl + endpoint.replace("{username}", username));
    }

    @When("I send a DELETE request to {string}")
    public void iSendADeleteRequestTo(String endpoint) {
        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestPayload)
                .delete(baseUrl + endpoint.replace("{username}", username));
    }

    @When("I send a PATCH request to {string}")
    public void iSendAPatchRequestTo(String endpoint) {
        response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestPayload)
                .patch(baseUrl + endpoint.replace("{username}", username));
    }

    @Then("the response status code should be {int}")
    public void the_response_status_code_should_be(Integer expectedStatusCode) {
        Assert.assertEquals("Unexpected status code", Optional.ofNullable(expectedStatusCode), response.getStatusCode());
    }

}