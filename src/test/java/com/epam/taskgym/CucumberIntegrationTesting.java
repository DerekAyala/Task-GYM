package com.epam.taskgym;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "com.epam.taskgym/src/test/resources/features",
        glue = "com.epam.taskgym.integration"
)
public class CucumberIntegrationTesting {
}
