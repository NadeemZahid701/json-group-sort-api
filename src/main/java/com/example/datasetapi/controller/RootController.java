package com.example.datasetapi.controller;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    @GetMapping("/")
    public Map<String, Object> root() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("service", "JSON Dataset Group By and Sort By API");
        response.put("version", "1.0.0");
        response.put("documentation", "/swagger-ui/index.html");
        response.put("openapi", "/v3/api-docs");
        response.put("health", "/actuator/health");
        return response;
    }
}
