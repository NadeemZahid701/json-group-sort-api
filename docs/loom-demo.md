# Loom Demo Script

This is a short demonstration sequence for recording a live API walkthrough.

## Demo Flow

1. Start the application with PostgreSQL running.
2. Open Swagger UI at http://localhost:9092/swagger-ui/index.html
3. POST a sample record to the `employee_dataset` dataset.
4. Query grouped records by `department`.
5. Show the response structure for grouped JSON data.
6. Mention that invalid dataset names and bad query parameters are rejected with structured errors.

## Example Commands

```bash
curl -X POST http://localhost:9092/api/dataset/employee_dataset/record \
  -H "Content-Type: application/json" \
  -d '{"id":1,"name":"John Doe","age":30,"department":"Engineering"}'

curl "http://localhost:9092/api/dataset/employee_dataset/query?groupBy=department&sortBy=age&order=asc"
```

## Talking Points

- The API persists records in a single metadata table with JSON content.
- Grouping and sorting happen dynamically at query time.
- The service validates field names and ordering rules before returning results.
- Swagger exposes all endpoints and schemas for easy inspection.
