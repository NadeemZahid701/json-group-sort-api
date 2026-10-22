package com.example.datasetapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI datasetApiOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("JSON Dataset Group By and Sort By API")
                        .version("1.0.0")
                        .description("REST API for storing JSON records in named datasets and querying them with dynamic grouping and sorting."));
    }
}
