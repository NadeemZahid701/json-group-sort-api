# JSON Group Sort API - Loom Presentation Script

## Project Overview
This project is a Spring Boot backend API that stores JSON records under a dataset name and lets the client query those records by grouping and sorting fields dynamically.

We built a complete working backend with:
- Java 21
- Spring Boot 4.1.1
- PostgreSQL database support
- JPA entity persistence for JSON records
- REST API endpoints for creating and querying datasets
- Input validation and custom error handling
- Swagger/OpenAPI documentation
- Docker support for local database setup
- Automated tests for service and controller behavior

## Main Features

### 1. Add records to a dataset
Endpoint:
POST /api/dataset/{datasetName}/record

Example payload:
{
  "id": 1,
  "name": "John Doe",
  "age": 30,
  "department": "Engineering"
}

This stores the record under the dataset name, such as `employee_dataset`.

### 2. Query grouped records
Endpoint:
GET /api/dataset/{datasetName}/query

Example:
/api/dataset/employee_dataset/query?groupBy=department&sortBy=age&order=asc

This groups records by the chosen field and returns results in a structured JSON response.

### 3. Validation and error handling
The API validates:
- dataset names
- groupBy and sortBy fields
- invalid sort orders
- malformed JSON input
- unknown fields or unsupported values

It returns clear JSON error messages instead of crashing.

### 4. Swagger UI
Swagger is enabled so the API can be explored and tested in the browser:
- http://localhost:8080/swagger-ui.html

### 5. Health endpoint
The app exposes health info:
- http://localhost:8080/actuator/health

## Architecture
We used a clean backend structure:
- Controller layer for API endpoints
- Service layer for business logic and validation
- Repository layer for database access
- Entity class for storing JSON data in PostgreSQL
- Exception handler for consistent API errors

## Database Design
The data is stored in a single table with:
- dataset_name
- record_data (JSONB/JSON payload)

This allows flexible record storage while still supporting dataset-specific queries.

## Testing
We implemented tests for:
- service logic for grouping and sorting
- controller behavior for successful creation and query responses
- validation flows and error handling

## Local Setup
Database:
```bash
docker compose up -d
```

Run the application:
```bash
mvn spring-boot:run
```

Open the app in browser:
```text
http://localhost:9090/swagger-ui.html
```

## What we accomplished
This project demonstrates a complete backend assignment with:
- working REST API
- database persistence
- dynamic group/sort queries
- validation and error management
- documentation and local demonstration support

## Final note for Loom

'We built a complete Spring Boot backend API that stores JSON records by dataset, groups them dynamically, sorts them by fields like age or department, validates bad input, and exposes everything through Swagger for easy testing. The app is container-ready, test-backed, and runs locally with PostgreSQL.'
