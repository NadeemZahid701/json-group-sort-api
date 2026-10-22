package com.example.datasetapi.service;

import com.example.datasetapi.dto.DatasetQueryResponse;
import com.example.datasetapi.dto.RecordCreationResponse;
import com.example.datasetapi.entity.DatasetRecordEntity;
import com.example.datasetapi.exception.InvalidDatasetNameException;
import com.example.datasetapi.exception.InvalidJsonQueryException;
import com.example.datasetapi.exception.InvalidQueryParameterException;
import com.example.datasetapi.exception.UnsupportedJsonValueException;
import com.example.datasetapi.repository.DatasetRecordRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class DatasetRecordService {

    private static final Pattern DATASET_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{1,100}$");

    private final DatasetRecordRepository repository;

    public DatasetRecordService(DatasetRecordRepository repository) {
        this.repository = repository;
    }

    public RecordCreationResponse addRecord(String datasetName, JsonNode payload) {
        String validatedDatasetName = validateDatasetName(datasetName);
        if (payload == null || payload.isNull() || !payload.isObject()) {
            throw new InvalidJsonQueryException("Request body must be a JSON object.");
        }

        DatasetRecordEntity entity = new DatasetRecordEntity(validatedDatasetName, payload.deepCopy());
        DatasetRecordEntity saved = repository.save(entity);
        return new RecordCreationResponse("Record added successfully", validatedDatasetName, saved.getId());
    }

    public DatasetQueryResponse queryDataset(String datasetName, String groupBy, String sortBy, String order) {
        String validatedDatasetName = validateDatasetName(datasetName);
        String normalizedOrder = normalizeOrder(order);

        List<DatasetRecordEntity> records = repository.findByDatasetNameOrderByIdAsc(validatedDatasetName);
        List<JsonNode> jsonRecords = records.stream().map(DatasetRecordEntity::getRecordData).toList();

        if (groupBy == null && sortBy == null) {
            return new DatasetQueryResponse(validatedDatasetName, null, null, normalizedOrder, jsonRecords.size(), null,
                    toPlainJsonList(jsonRecords));
        }

        if (groupBy != null && !groupBy.isBlank() && records.isEmpty()) {
            return new DatasetQueryResponse(validatedDatasetName, groupBy, sortBy, normalizedOrder, 0,
                    new LinkedHashMap<>(), List.of());
        }

        if (groupBy != null && !groupBy.isBlank()) {
            if (!records.isEmpty() && !fieldExistsInAnyRecord(jsonRecords, groupBy)) {
                throw new InvalidQueryParameterException("Grouping field '" + groupBy + "' does not exist in any record in dataset '" + validatedDatasetName + "'.");
            }
        }

        if (sortBy != null && !sortBy.isBlank()) {
            if (!records.isEmpty() && !fieldExistsInAnyRecord(jsonRecords, sortBy)) {
                throw new InvalidQueryParameterException("Sorting field '" + sortBy + "' does not exist in any record in dataset '" + validatedDatasetName + "'.");
            }
        }

        Map<String, List<Object>> groupedRecords = new LinkedHashMap<>();
        if (groupBy != null && !groupBy.isBlank()) {
            groupedRecords = groupRecordsAsPlainJson(jsonRecords, groupBy, sortBy, normalizedOrder);
            int count = jsonRecords.size();
            return new DatasetQueryResponse(validatedDatasetName, groupBy, sortBy, normalizedOrder, count,
                    groupedRecords, null);
        }

        List<Object> sortedRecords = toPlainJsonList(sortRecords(jsonRecords, sortBy, normalizedOrder));
        return new DatasetQueryResponse(validatedDatasetName, null, sortBy, normalizedOrder, jsonRecords.size(), null,
                sortedRecords);
    }

    private List<Object> toPlainJsonList(List<JsonNode> records) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Object> plain = new ArrayList<>();
        for (JsonNode record : records) {
            plain.add(objectMapper.convertValue(record, Object.class));
        }
        return plain;
    }

    private Map<String, List<Object>> groupRecordsAsPlainJson(List<JsonNode> records, String groupBy, String sortBy, String order) {
        Map<String, List<JsonNode>> grouped = groupRecords(records, groupBy, sortBy, order);
        Map<String, List<Object>> plain = new LinkedHashMap<>();
        for (Map.Entry<String, List<JsonNode>> entry : grouped.entrySet()) {
            plain.put(entry.getKey(), toPlainJsonList(entry.getValue()));
        }
        return plain;
    }

    private Map<String, List<JsonNode>> groupRecords(List<JsonNode> records, String groupBy, String sortBy, String order) {
        Map<String, List<JsonNode>> grouped = new LinkedHashMap<>();
        for (JsonNode record : records) {
            if (record == null || !record.isObject()) {
                throw new InvalidJsonQueryException("Dataset records must be JSON objects.");
            }

            String groupKey = resolveGroupKey(record, groupBy);
            grouped.computeIfAbsent(groupKey, ignored -> new ArrayList<>()).add(record);
        }

        if (sortBy != null && !sortBy.isBlank()) {
            for (Map.Entry<String, List<JsonNode>> entry : grouped.entrySet()) {
                entry.setValue(sortRecords(entry.getValue(), sortBy, order));
            }
        }
        return grouped;
    }

    private String resolveGroupKey(JsonNode record, String fieldName) {
        if (!record.has(fieldName)) {
            return "MISSING";
        }
        JsonNode value = record.get(fieldName);
        if (value == null || value.isNull()) {
            return "NULL";
        }
        if (value.isContainerNode()) {
            return value.toString();
        }
        if (value.isBoolean()) {
            return Boolean.toString(value.booleanValue());
        }
        if (value.isNumber()) {
            return value.decimalValue().stripTrailingZeros().toPlainString();
        }
        return value.asText();
    }

    private List<JsonNode> sortRecords(List<JsonNode> records, String sortBy, String order) {
        if (records == null || records.isEmpty() || sortBy == null || sortBy.isBlank()) {
            return new ArrayList<>(records);
        }

        List<JsonNode> sorted = new ArrayList<>(records);
        sorted.sort((left, right) -> compareValues(left, right, sortBy, order));
        return sorted;
    }

    private int compareValues(JsonNode left, JsonNode right, String sortBy, String order) {
        JsonNode leftValue = left != null && left.isObject() && left.has(sortBy) ? left.get(sortBy) : null;
        JsonNode rightValue = right != null && right.isObject() && right.has(sortBy) ? right.get(sortBy) : null;

        if (leftValue == null && rightValue == null) {
            return 0;
        }
        if (leftValue == null) {
            return 1;
        }
        if (rightValue == null) {
            return -1;
        }

        int comparison = compareNodeValues(leftValue, rightValue);
        return "desc".equalsIgnoreCase(order) ? -comparison : comparison;
    }

    private int compareNodeValues(JsonNode left, JsonNode right) {
        if (left == null || right == null) {
            return 0;
        }

        if (left.isNumber() && right.isNumber()) {
            BigDecimal leftDecimal = left.decimalValue();
            BigDecimal rightDecimal = right.decimalValue();
            return leftDecimal.compareTo(rightDecimal);
        }
        if (left.isTextual() && right.isTextual()) {
            return left.asText().compareToIgnoreCase(right.asText());
        }
        if (left.isBoolean() && right.isBoolean()) {
            return Boolean.compare(left.booleanValue(), right.booleanValue());
        }
        if (left.isContainerNode() || right.isContainerNode()) {
            throw new UnsupportedJsonValueException("Unsupported value type for sorting field. Arrays and objects must be serialized or transformed before sorting.");
        }

        if (left.isNull() || right.isNull()) {
            return 0;
        }

        return left.toString().compareToIgnoreCase(right.toString());
    }

    private boolean fieldExistsInAnyRecord(List<JsonNode> records, String fieldName) {
        for (JsonNode record : records) {
            if (record != null && record.isObject() && record.has(fieldName)) {
                return true;
            }
        }
        return false;
    }

    private String validateDatasetName(String datasetName) {
        if (datasetName == null || datasetName.isBlank()) {
            throw new InvalidDatasetNameException("Dataset name is required.");
        }

        String sanitized = datasetName.trim();
        if (!DATASET_NAME_PATTERN.matcher(sanitized).matches()) {
            throw new InvalidDatasetNameException("Dataset name must be 1-100 characters and contain only letters, numbers, underscores, or hyphens.");
        }
        return sanitized;
    }

    private String normalizeOrder(String order) {
        if (order == null || order.isBlank()) {
            return "asc";
        }
        String normalized = order.trim().toLowerCase(Locale.ROOT);
        if (!"asc".equals(normalized) && !"desc".equals(normalized)) {
            throw new InvalidQueryParameterException("Order must be 'asc' or 'desc'.");
        }
        return normalized;
    }
}
