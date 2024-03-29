Feature: Gym Management System

  Scenario: Successfully add a new trainee
    Given I prepare a POST request to "/api/trainee" with valid trainee details
    When I send the request
    Then the response status code should be 201
    And the response should include successful registration details

  Scenario: Successfully add a new trainer
    Given I prepare a POST request to "/api/trainer" with valid trainer details
    When I send the request
    Then the response status code should be 201
    And the response should include successful registration details

  Scenario: Successfully log in a user
    Given I prepare a GET request to "/api/user/login" with valid credentials
    When I send the request
    Then the response status code should be 200
    And the response should contain login details

  Scenario: Successfully update password
    Given I prepare a PUT request to "/api/user/{username}/password" with a new password
    When I send the request
    Then the response status code should be 200

  Scenario: Retrieve trainee profile by username
    Given I prepare a GET request to "/api/trainees/{username}"
    When I send the request
    Then the response status code should be 200
    And the response should contain the trainee's profile details

  Scenario: Update trainee profile
    Given I prepare a PUT request to "/api/trainees/{username}" with updated trainee details
    When I send the request
    Then the response status code should be 200
    And the response should reflect the updated trainee details

  Scenario: Delete trainee profile
    Given I prepare a DELETE request to "/api/trainees/{username}"
    When I send the request
    Then the response status code should be 200
    And the response should confirm the deletion

  Scenario: Retrieve trainer profile by username
    Given I prepare a GET request to "/api/trainers/{username}"
    When I send the request
    Then the response status code should be 200
    And the response should contain the trainer's profile details

  Scenario: Update trainer profile
    Given I prepare a PUT request to "/api/trainers/{username}" with updated trainer details
    When I send the request
    Then the response status code should be 200
    And the response should reflect the updated trainer details

  Scenario: Get list of not assigned active trainers for a trainee
    Given I prepare a GET request to "/api/trainees/{username}/trainersNotAssigned"
    When I send the request
    Then the response status code should be 200
    And the response should list unassigned trainers

  Scenario: Update Trainee's Trainer List
    Given I prepare a PUT request to "/api/trainees/{username}/trainers" with a list of trainer usernames
    When I send the request
    Then the response status code should be 200
    And the response should include the updated list of assigned trainers

  Scenario: Get Trainee Trainings List
    Given I prepare a GET request to "/api/trainees/{username}/trainings" with filter criteria
    When I send the request
    Then the response status code should be 200
    And the response should include a filtered list of trainings

  Scenario: Get Trainer Trainings List
    Given I prepare a GET request to "/api/trainers/{username}/trainings" with filter criteria
    When I send the request
    Then the response status code should be 200
    And the response should include a filtered list of trainings

  Scenario: Successfully add training
    Given I prepare a POST request to "/api/training" with training details
    When I send the request
    Then the response status code should be 201
    And the response should include training creation details

  Scenario: Activate/Deactivate Trainee
    Given I prepare a PATCH request to "/api/trainees/{username}/status" with activate/deactivate action
    When I send the request
    Then the response status code should be 200
    And the trainee status should be updated accordingly

  Scenario: Activate/Deactivate Trainer
    Given I prepare a PATCH request to "/api/trainers/{username}/status" with activate/deactivate action
    When I send the request
    Then the response status code should be 200
    And the trainer status should be updated accordingly

  Scenario: Get All Training Types
    Given I prepare a GET request to "/api/training-Types"
    When I send the request
    Then the response status code should be 200
    And the response should list all training types