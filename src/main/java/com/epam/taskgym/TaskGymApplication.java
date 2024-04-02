package com.epam.taskgym;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class TaskGymApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskGymApplication.class, args);
	}

}
