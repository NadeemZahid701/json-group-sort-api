package com.example.datasetapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecordCreationResponse {

    private final String message;
    private final String dataset;
    private final Long recordId;

    public RecordCreationResponse(String message, String dataset, Long recordId) {
        this.message = message;
        this.dataset = dataset;
        this.recordId = recordId;
    }

    public String getMessage() {
        return message;
    }

    public String getDataset() {
        return dataset;
    }

    public Long getRecordId() {
        return recordId;
    }
}
