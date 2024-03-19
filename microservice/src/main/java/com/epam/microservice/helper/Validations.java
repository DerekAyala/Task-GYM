package com.epam.microservice.helper;

import com.epam.microservice.exception.MissingAttributes;
import com.epam.microservice.model.TrainingRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class Validations {
    private static final Logger LOGGER = LoggerFactory.getLogger(Validations.class);

    public static void validateTrainingRequest(TrainingRequest trainingRequest) {
        LOGGER.info("Transaction Id: {}, Validating training request: {}", MDC.get("transactionId"), trainingRequest);
        if (trainingRequest == null) {
            LOGGER.error("Transaction Id: {}, Training request is required", MDC.get("transactionId"));
            throw new MissingAttributes("Training request is required");
        }
    }

    public static void validateTrainingRequestForAdd(TrainingRequest trainingRequest) {
        LOGGER.info("Transaction Id: {}, Validating training request for add: {}", MDC.get("transactionId"), trainingRequest);
        if ((trainingRequest.getFirstName() == null || trainingRequest.getFirstName().isEmpty()) ||
                (trainingRequest.getLastName() == null || trainingRequest.getLastName().isEmpty()) ||
                (trainingRequest.getUsername() == null || trainingRequest.getUsername().isEmpty()) ||
                (trainingRequest.getDate() == null) ||
                (trainingRequest.getIsActive() == null)) {
            LOGGER.error("Transaction Id: {}, First name, last name, username, date and status are required fields", MDC.get("transactionId"));
            throw new MissingAttributes("First name, last name, username, date and status are required fields");
        }
    }

    public static void validateTrainingRequestForDelete(TrainingRequest trainingRequest) {
        LOGGER.info("Transaction Id: {}, Validating training request for delete: {}", MDC.get("transactionId"), trainingRequest);
        if ((trainingRequest.getUsername() == null || trainingRequest.getUsername().isEmpty()) ||
                (trainingRequest.getDate() == null)) {
            LOGGER.error("Transaction Id: {}, Username and date is required", MDC.get("transactionId"));
            throw new MissingAttributes("Username and date is required");
        }
    }

    public static void validateAction(TrainingRequest trainingRequest) {
        LOGGER.info("Transaction Id: {}, Validating action: {}", MDC.get("transactionId"), trainingRequest.getAction());
        if (trainingRequest.getAction() == null || trainingRequest.getAction().isEmpty()) {
            LOGGER.error("Transaction Id: {}, Action is required", MDC.get("transactionId"));
            throw new MissingAttributes("Action is required");
        }
        if (!trainingRequest.getAction().equalsIgnoreCase("add") && !trainingRequest.getAction().equalsIgnoreCase("delete")) {
            LOGGER.error("Transaction Id: {}, Action must be add or delete", MDC.get("transactionId"));
            throw new MissingAttributes("Action must be add or delete");
        }
    }
}
