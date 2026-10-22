# JSON Group Sort API - Loom Presentation Script

## Approach
I built a Spring Boot backend API that stores JSON records under a dataset name and allows us to query those records dynamically by grouping and sorting different fields. The goal was to create a simple but realistic backend service where data can be added in a flexible JSON format and then retrieved in a structured, grouped way.

This project includes Java 21, Spring Boot, JPA, validation, custom exception handling, Swagger documentation, and database support. The core idea is that the API does not assume a fixed schema for every record. Instead, it accepts JSON payloads, stores them under a dataset, and then lets the user run queries such as grouping by department and sorting by age.

So the app solves a very practical problem: storing unstructured or semi-structured records and then retrieving them in a grouped, meaningful format for reporting, filtering, or analysis.

## API Demo
Now I’ll show the actual API workflow in Swagger, which is running locally at:

http://localhost:9092/swagger-ui/index.html

First, I demonstrate the POST endpoint. This endpoint allows us to add a new record into a dataset such as employee_dataset. The payload contains fields like id, name, age, and department. When the request is sent, the server validates the dataset name and the JSON body, then stores the record successfully.

After that, I open the GET query endpoint. This is the main business logic of the application. I pass parameters like:
- datasetName = employee_dataset
- groupBy = department
- sortBy = age
- order = asc

The API then groups records by department and sorts them by age in ascending order. In the response, we can see the groupedRecords structure being returned with the relevant records organized under each department. This shows the API is working as intended and not just accepting input but actually processing it correctly.

This is the most important demonstration because it proves the core assignment requirement: storing JSON data and returning grouped, sorted records based on query parameters.

## Technical Decisions
One important decision was to support local development smoothly. The app is configured to run with an H2 in-memory database by default, while still being compatible with PostgreSQL when needed. This was useful because the environment did not always have PostgreSQL running, and we wanted the app to boot reliably for testing and demo work.

Another key decision was to build robust validation and structured error handling. The API validates dataset names, invalid groupBy or sortBy fields, unsupported values, malformed JSON, and invalid sort orders. Instead of failing with a generic server error, it returns clean JSON responses. That makes the API more reliable, easier to test, and much easier to demo.

## Challenge
One of the main problems I faced was runtime and environment instability during startup. The app initially had port conflicts and the local environment was not always configured for PostgreSQL. I solved this by switching the default runtime to H2 so the app could boot locally without external infrastructure, and I also used a non-conflicting port during the demo so Swagger was reachable reliably.

This was really important because the application logic itself was fine, but the environment prevented it from starting cleanly. Once the configuration and port issues were fixed, the API ran successfully, Swagger became accessible, and the POST and GET flows worked as expected.

## Final Closing
So in short, this project is a working backend API for storing JSON data, grouping and sorting it dynamically, and exposing the functionality through Swagger for easy testing. The app demonstrates a complete assignment flow: data is added, validated, queried, grouped, sorted, and returned in a clear JSON response. That is the core value of this project, and it is exactly what I wanted to show in the demonstration.
