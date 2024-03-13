package com.epam.microservice.helper;

import com.epam.microservice.exception.MissingAttributes;
import com.epam.microservice.model.TrainingRequest;

public class Validations {

    public static void validateTrainingRequest(TrainingRequest trainingRequest) {
        if (trainingRequest == null) {
            throw new MissingAttributes("Training request is required");
        }
    }
    public static void validateTrainingRequestForAdd(TrainingRequest trainingRequest) {
        if ((trainingRequest.getFirstName() == null || trainingRequest.getFirstName().isEmpty()) ||
                (trainingRequest.getLastName() == null || trainingRequest.getLastName().isEmpty()) ||
                (trainingRequest.getUsername() == null || trainingRequest.getUsername().isEmpty()) ||
                (trainingRequest.getDate() == null) ||
                (trainingRequest.getIsActive() == null)){
            throw new MissingAttributes("First name, last name, username, date and status are required fields");
        }
    }

    public static void validateTrainingRequestForDelete(TrainingRequest trainingRequest) {
        if (trainingRequest.getUsername() == null || trainingRequest.getUsername().isEmpty()) {
            throw new MissingAttributes("Username is required");
        }
    }

    public static void validateAction(TrainingRequest trainingRequest) {
        if (trainingRequest.getAction() == null || trainingRequest.getAction().isEmpty()) {
            throw new MissingAttributes("Action is required");
        }
        if (!trainingRequest.getAction().equalsIgnoreCase("add") && !trainingRequest.getAction().equalsIgnoreCase("delete")) {
            throw new MissingAttributes("Action must be add or delete");
        }
    }
}
