package com.epam.taskgym.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Derek Ayala",
                        email = "derek_ayalah@epam.com",
                        url = "https://www.linkedin.com/in/derekayala/"
                ),
                description = "Open API Documentation for THE GYM",
                title = "Open API Specification - GYM",
                version = "1.0"
        ),
        servers = @Server(
                description = "LOCAL ENV",
                url = "http://localhost:8080"
        )
)
public class OpenApiConfig {

}
