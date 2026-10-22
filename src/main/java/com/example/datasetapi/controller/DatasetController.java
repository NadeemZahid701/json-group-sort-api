package com.example.datasetapi.controller;

import com.example.datasetapi.dto.DatasetQueryResponse;
import com.example.datasetapi.dto.RecordCreationResponse;
import com.example.datasetapi.service.DatasetRecordService;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Dataset records", description = "Create JSON records and query them with grouping and sorting")
public class DatasetController {

    private final DatasetRecordService datasetRecordService;

    public DatasetController(DatasetRecordService datasetRecordService) {
        this.datasetRecordService = datasetRecordService;
    }

    @PostMapping("/dataset/{datasetName}/record")
    @Operation(
            summary = "Insert a JSON record into a named dataset",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(value = "{\"id\": 1, \"name\": \"John Doe\", \"age\": 30, \"department\": \"Engineering\"}")
                            }
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Record added successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid dataset name or JSON payload")
            }
    )
    public ResponseEntity<RecordCreationResponse> createRecord(
            @Parameter(description = "Dataset name. Allowed pattern: letters, digits, underscores, and hyphens; max 100 chars.")
            @PathVariable String datasetName,
            @RequestBody JsonNode payload) {
        RecordCreationResponse response = datasetRecordService.addRecord(datasetName, payload);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/dataset/{datasetName}/query")
    @Operation(
            summary = "Query a dataset with optional dynamic grouping and sorting",
            description = "Use groupBy and/or sortBy to inspect records in a dataset. If neither is supplied, records are returned in insertion order.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful query result"),
                    @ApiResponse(responseCode = "400", description = "Invalid query parameters or missing field")
            }
    )
    public ResponseEntity<DatasetQueryResponse> queryDataset(
            @Parameter(description = "Dataset name")
            @PathVariable String datasetName,
            @Parameter(description = "Top-level field used to group records")
            @RequestParam(required = false) String groupBy,
            @Parameter(description = "Top-level field used to sort records")
            @RequestParam(required = false) String sortBy,
            @Parameter(description = "Sort direction: asc or desc")
            @RequestParam(defaultValue = "asc") String order) {
        DatasetQueryResponse response = datasetRecordService.queryDataset(datasetName, groupBy, sortBy, order);
        return ResponseEntity.ok(response);
    }
}
