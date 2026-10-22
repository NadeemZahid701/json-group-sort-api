FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
COPY . .
RUN chmod +x mvnw
RUN ./mvnw -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
ENV PORT=8080
COPY --from=build /workspace/target/json-group-sort-api-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -jar /app/app.jar --server.port=${PORT}"]
