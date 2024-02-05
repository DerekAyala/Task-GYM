package com.epam.taskgym;

import com.epam.taskgym.storage.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public UserInMemoryDb userDb() {
        return new UserInMemoryDb();
    }

    @Bean
    public TraineeInMemoryDb traineeDb() {
        return new TraineeInMemoryDb();
    }

    @Bean
    public TrainerInMemoryDb trainerDb() {
        return new TrainerInMemoryDb();
    }

    @Bean
    public TrainingInMemoryDb trainingDb() {
        return new TrainingInMemoryDb();
    }

    @Bean
    public TrainingTypeInMemoryDb trainingTypeDb() {
        return new TrainingTypeInMemoryDb();
    }

}
