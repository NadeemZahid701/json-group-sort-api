package com.example.datasetapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DatasetQueryResponse {

    private final String dataset;
    private final String groupBy;
    private final String sortBy;
    private final String order;
    private final int count;
    private final Map<String, List<JsonNode>> groupedRecords;
    private final List<JsonNode> sortedRecords;

    public DatasetQueryResponse(String dataset, String groupBy, String sortBy, String order, int count,
                                Map<String, List<JsonNode>> groupedRecords, List<JsonNode> sortedRecords) {
        this.dataset = dataset;
        this.groupBy = groupBy;
        this.sortBy = sortBy;
        this.order = order;
        this.count = count;
        this.groupedRecords = groupedRecords;
        this.sortedRecords = sortedRecords;
    }

    public String getDataset() {
        return dataset;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public String getSortBy() {
        return sortBy;
    }

    public String getOrder() {
        return order;
    }

    public int getCount() {
        return count;
    }

    public Map<String, List<JsonNode>> getGroupedRecords() {
        return groupedRecords;
    }

    public List<JsonNode> getSortedRecords() {
        return sortedRecords;
    }
}
