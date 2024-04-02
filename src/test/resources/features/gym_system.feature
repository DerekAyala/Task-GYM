Feature: Gym Management System

  Scenario: Successfully add a new trainee
    Given valid trainee details
    When I send a POST request to "/api/trainee"
    Then the response status code should be 201

  Scenario: Successfully add a new trainer
    Given valid trainer details
    When I send a POST request to "/api/trainer"
    Then the response status code should be 201

  Scenario: Successfully log in a user
    Given valid credentials
    When I send a GET request to "/api/user/login"
    Then the response status code should be 200

  Scenario: Successfully update a password
    Given valid username "johndoe" and a new password "newPassword"
    When I send a PUT request to "/api/user/{username}/password"
    Then the response status code should be 200

  Scenario: Retrieve trainee profile by username
    Given valid trainee username "johndoe"
    When I send a GET request to "/api/trainees/{username}"
    Then the response status code should be 200

  Scenario: Update trainee profile
    Given valid trainee username "johndoe" and updated trainee details
    When I send a PUT request to "/api/trainees/{username}"
    Then the response status code should be 200

  Scenario: Delete trainee profile
    Given valid trainee username "johndoe"
    When I send a DELETE request to "/api/trainees/{username}"
    Then the response status code should be 200

  Scenario: Retrieve trainer profile by username
    Given valid trainer username "johndoe"
    When I send a GET request to "/api/trainers/{username}"
    Then the response status code should be 200

  Scenario: Update trainer profile
    Given valid trainer username "johndoe" and updated trainer details
    When I send a PUT request to "/api/trainers/{username}"
    Then the response status code should be 200

  Scenario: Get list of not assigned active trainers for a trainee
    Given valid trainee username "johndoe"
    When I send a GET request to "/api/trainees/{username}/trainersNotAssigned"
    Then the response status code should be 200

  Scenario: Get Trainee Trainings List
    Given valid trainee username "johndoe" and filter criteria
    When I send a GET request to "/api/trainees/{username}/trainings"
    Then the response status code should be 200

  Scenario: Get Trainer Trainings List
    Given valid trainer username "johndoe" and filter criteria
    When I send a GET request to "/api/trainers/{username}/trainings"
    Then the response status code should be 200

  Scenario: Successfully add training
    Given valid training details
    When I send a POST request to "/api/training"
    Then the response status code should be 201

  Scenario: Get All Training Types
    Given valid request
    When I send a GET request to "/api/training-Types"
    Then the response status code should be 200