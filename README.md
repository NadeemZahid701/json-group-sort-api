# JSON Group Sort API

A Spring Boot backend service that stores JSON records under a dataset name and allows grouped, sorted queries against them.

## Features

- Add a JSON record to a dataset with `POST /api/dataset/{datasetName}/record`
- Query grouped results with `GET /api/dataset/{datasetName}/query`
- Validate dataset names and query parameters consistently
- Return structured JSON error responses for invalid input
- Expose Swagger UI and OpenAPI docs
- Support local PostgreSQL setup via Docker Compose

## Tech Stack

- Java 21
- Spring Boot 4.1.1
- Spring Data JPA
- PostgreSQL
- H2 for tests
- Springdoc OpenAPI
- Maven

## Local Setup

1. Start PostgreSQL locally:

   ```bash
   docker compose up -d
   ```

2. Build and run the app:

   ```bash
   mvn spring-boot:run
   ```

3. Open:
   - Swagger UI: http://localhost:9092/swagger-ui/index.html
   - Actuator health: http://localhost:9092/actuator/health

## Example Requests

### Add a record

```bash
curl -X POST http://localhost:9092/api/dataset/employee_dataset/record \
  -H "Content-Type: application/json" \
  -d '{"id":1,"name":"John Doe","age":30,"department":"Engineering"}'
```

### Query grouped records

```bash
curl "http://localhost:9092/api/dataset/employee_dataset/query?groupBy=department&sortBy=age&order=asc"
```

## Project Status

- Core backend implementation complete
- Full Maven test suite passes using the local Java/Maven installation in this environment
- Maven wrapper download is not usable in this offline environment, so the project should be run with the installed Maven toolchain when needed

## Notes

This repository is prepared for local development and demonstration. If you publish it to GitHub, use a repo name like:

```text
https://github.com/<your-github-username>/json-group-sort-api
```
