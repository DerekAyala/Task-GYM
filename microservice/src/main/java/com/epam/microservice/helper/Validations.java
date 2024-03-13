package com.epam.microservice.helper;

import com.epam.microservice.exception.MissingAttributes;
import com.epam.microservice.model.TrainingRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Validations {
    private static final Logger LOGGER = LoggerFactory.getLogger(Validations.class);

    public static void validateTrainingRequest(TrainingRequest trainingRequest) {
        LOGGER.info("Validating training request is not null");
        if (trainingRequest == null) {
            throw new MissingAttributes("Training request is required");
        }
    }

    public static void validateTrainingRequestForAdd(TrainingRequest trainingRequest) {
        LOGGER.info("Validating training request for add: {}", trainingRequest);
        if ((trainingRequest.getFirstName() == null || trainingRequest.getFirstName().isEmpty()) ||
                (trainingRequest.getLastName() == null || trainingRequest.getLastName().isEmpty()) ||
                (trainingRequest.getUsername() == null || trainingRequest.getUsername().isEmpty()) ||
                (trainingRequest.getDate() == null) ||
                (trainingRequest.getIsActive() == null)) {
            throw new MissingAttributes("First name, last name, username, date and status are required fields");
        }
    }

    public static void validateTrainingRequestForDelete(TrainingRequest trainingRequest) {
        LOGGER.info("Validating training request for delete: {}", trainingRequest);
        if ((trainingRequest.getUsername() == null || trainingRequest.getUsername().isEmpty()) ||
                (trainingRequest.getDate() == null)) {
            throw new MissingAttributes("Username and date is required");
        }
    }

    public static void validateAction(TrainingRequest trainingRequest) {
        LOGGER.info("Validating action: {}", trainingRequest.getAction());
        if (trainingRequest.getAction() == null || trainingRequest.getAction().isEmpty()) {
            throw new MissingAttributes("Action is required");
        }
        if (!trainingRequest.getAction().equalsIgnoreCase("add") && !trainingRequest.getAction().equalsIgnoreCase("delete")) {
            throw new MissingAttributes("Action must be add or delete");
        }
    }
}
