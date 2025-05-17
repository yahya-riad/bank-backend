package com.test.bank.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(info = @Info(title = "Bank API", version = "v1", description = "API documentation for the E-Commerce application"))
@Configuration
public class OpenApiConfig {
}
