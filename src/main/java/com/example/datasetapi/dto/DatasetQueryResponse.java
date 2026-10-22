package com.example.datasetapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DatasetQueryResponse {

    private final String dataset;
    private final String groupBy;
    private final String sortBy;
    private final String order;
    private final int count;
    private final Map<String, List<Object>> groupedRecords;
    private final List<Object> sortedRecords;

    public DatasetQueryResponse(String dataset, String groupBy, String sortBy, String order, int count,
                                Map<String, List<Object>> groupedRecords, List<Object> sortedRecords) {
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

    public Map<String, List<Object>> getGroupedRecords() {
        return groupedRecords;
    }

    public List<Object> getSortedRecords() {
        return sortedRecords;
    }
}
