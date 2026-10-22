# Project Summary

## 1. Project Name
JSON Group Sort API

## 2. Purpose
This project is a backend service built with Spring Boot that stores JSON records using a dataset name and supports dynamic grouping and sorting of those records through query parameters.

The system allows clients to:
- add records to a dataset
- query records by grouping field
- sort by any valid field in ascending or descending order
- validate data and reject invalid input gracefully

This is useful for APIs that need to ingest flexible JSON records and then return summarized or grouped results for analysis, reporting, or UI display.

## 3. Tech Stack Used
The project uses:

- Java 21
- Spring Boot 4.1.1
- Maven
- Spring Web
- Spring Data JPA
- PostgreSQL
- H2 Database (for test environment)
- Lombok (if used in project structure)
- Springdoc OpenAPI / Swagger UI
- Actuator for health checks
- Docker and Docker Compose
- JUnit 5 and Mockito for testing

## 4. Core Features Implemented

### A. Create a record in a dataset
The API accepts JSON data and stores it under a dataset name.

Example route:
POST /api/dataset/{datasetName}/record

Example request body:
{
  "id": 1,
  "name": "John Doe",
  "age": 30,
  "department": "Engineering"
}

The server validates that:
- the dataset name is valid
- the request contains proper JSON
- the record is not malformed

### B. Query grouped records
The API can return records grouped by a chosen field.

Example route:
GET /api/dataset/{datasetName}/query?groupBy=department&sortBy=age&order=asc

This means:
- group records by department
- sort each group by age
- arrange them in ascending order

The result is structured JSON where each group contains a list of matching records.

### C. Flexible JSON record storage
Instead of creating a separate table for every record type, the application stores each dataset item as JSON inside a single table.

This gives flexibility because different dataset records can have different fields without requiring schema changes.

### D. Validation and Error Handling
The project includes custom exceptions and a central exception handler to return clean and meaningful HTTP errors.

Examples of validation include:
- invalid dataset names
- invalid sort fields
- unsupported sort order
- unknown or missing fields
- malformed JSON requests

This keeps the API stable and easier to debug.

### E. Swagger Documentation
OpenAPI/Swagger is configured so the backend can be tested easily through a browser UI.

Swagger provides a way to:
- inspect endpoints
- understand request/response schemas
- test requests directly from the UI

### F. Health Monitoring
The app exposes actuator health endpoints to monitor runtime status.

Example:
- /actuator/health

## 5. Project Architecture
The project is organized into clear layers:

### Controller Layer
Responsible for handling HTTP requests and returning responses.

Example controller responsibilities:
- accept POST requests
- validate request parameters
- send data to service layer
- format JSON responses

### Service Layer
Contains the business logic.

This layer handles:
- dataset validation
- grouping logic
- sorting logic
- record addition logic
- query processing

### Repository Layer
Handles database operations.

It allows persistence of records by dataset name and retrieval of records for a dataset.

### Entity Layer
The database entity represents stored records.

It includes fields such as:
- dataset name
- record data

The JSON payload is stored in a column designed for JSON data, making it flexible for multiple record structures.

### Exception Layer
This handles invalid input and returns structured error responses like:
- 400 Bad Request
- 500 Internal Server Error

## 6. Database Design
The project uses PostgreSQL.

A common pattern here is one table for all records, where each row contains:
- dataset name
- JSON payload for the actual record

This is helpful because data can vary from one record to another, yet still be stored in a consistent schema.

For local development, Docker Compose is used to run PostgreSQL in a container.

## 7. How Grouping and Sorting Works
The API reads records from the dataset, checks the requested group field, and groups items based on matching values.

Example:
If records are:
- John Doe, department = Engineering
- Jane Smith, department = Engineering
- Alice Brown, department = Marketing

and the query is:
groupBy=department

then the response becomes:
{
  "Engineering": [ ... ],
  "Marketing": [ ... ]
}

Sorting is applied inside each group and may be in ascending or descending order, depending on the request.

This makes the response easy for frontend apps to consume.

## 8. Why This Project Is Useful
This project demonstrates a real-world backend pattern where:
- data arrives in JSON format
- data is stored in a flexible structure
- records are later queried and grouped dynamically
- business rules are enforced in the service layer
- results are returned in a clean API format

It is ideal for projects that need custom reporting, analytics, or dynamic dataset handling.

## 9. Tests Included
The project includes tests for:
- record creation
- grouped query results
- sorting behavior
- validation errors
- controller responses

This ensures the core behavior works as expected and makes the project more reliable.

## 10. Local Development Setup
To run the app locally:

1. Start PostgreSQL using Docker:
   docker compose up -d

2. Start the application:
   mvn spring-boot:run

3. Open Swagger UI:
   http://localhost:8080/swagger-ui.html

4. Check health status:
   http://localhost:8080/actuator/health

## 11. Important Notes
The project was built and verified to run using the local Java and Maven installation available in the environment.

The Maven wrapper was tried, but in this environment it failed because it attempted to download Maven from the internet while the environment is offline. The actual project verification was completed successfully using the installed Maven toolchain.

## 12. Final Summary for Presentation
This project is a complete Spring Boot backend that stores JSON records under dataset names, groups and sorts them dynamically, validates user input, and exposes the API through Swagger. It uses Postgres for persistence, layered Spring architecture for clean code, and test coverage for reliability. It is a strong demonstration of how a flexible dataset-based backend can be built in production-ready style.
