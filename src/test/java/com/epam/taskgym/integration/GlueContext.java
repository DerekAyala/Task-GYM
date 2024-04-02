package com.epam.taskgym.integration;

import com.epam.taskgym.TaskGymApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.After;
import org.junit.Before;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = TaskGymApplication.class)
public class GlueContext {
    @Before
    public void beforeScenario() {
        // You can add any setup logic here that needs to be executed before each scenario
        System.out.println("Before scenario setup...");
    }

    @After
    public void afterScenario() {
        // You can add any cleanup logic here that needs to be executed after each scenario
        System.out.println("After scenario cleanup...");
    }
}
