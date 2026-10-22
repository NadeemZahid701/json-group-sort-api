package com.example.datasetapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.datasetapi.dto.DatasetQueryResponse;
import com.example.datasetapi.dto.RecordCreationResponse;
import com.example.datasetapi.entity.DatasetRecordEntity;
import com.example.datasetapi.exception.InvalidDatasetNameException;
import com.example.datasetapi.exception.InvalidQueryParameterException;
import com.example.datasetapi.repository.DatasetRecordRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DatasetRecordServiceTest {

    @Mock
    private DatasetRecordRepository repository;

    private DatasetRecordService service;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        service = new DatasetRecordService(repository);
    }

    @Test
    void addRecord_shouldPersistAndReturnDatabaseGeneratedId() throws Exception {
        when(repository.save(any(DatasetRecordEntity.class))).thenAnswer(invocation -> {
            DatasetRecordEntity entity = invocation.getArgument(0);
            Field idField = DatasetRecordEntity.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, 42L);
            return entity;
        });

        JsonNode payload = objectMapper.readTree("{\"id\":1,\"name\":\"John Doe\",\"age\":30,\"department\":\"Engineering\"}");

        RecordCreationResponse response = service.addRecord("employee_dataset", payload);

        assertEquals("Record added successfully", response.getMessage());
        assertEquals("employee_dataset", response.getDataset());
        assertEquals(42L, response.getRecordId());
    }

    @Test
    void queryDataset_shouldGroupByDepartment() throws Exception {
        List<DatasetRecordEntity> records = List.of(
                record("employee_dataset", "{\"id\":1,\"name\":\"John Doe\",\"age\":30,\"department\":\"Engineering\"}"),
                record("employee_dataset", "{\"id\":2,\"name\":\"Jane Smith\",\"age\":25,\"department\":\"Engineering\"}"),
                record("employee_dataset", "{\"id\":3,\"name\":\"Alice Brown\",\"age\":28,\"department\":\"Marketing\"}")
        );
        when(repository.findByDatasetNameOrderByIdAsc("employee_dataset")).thenReturn(records);

        DatasetQueryResponse response = service.queryDataset("employee_dataset", "department", null, "asc");

        assertEquals("employee_dataset", response.getDataset());
        assertEquals(3, response.getCount());
        assertNotNull(response.getGroupedRecords().get("Engineering"));
        assertEquals(2, response.getGroupedRecords().get("Engineering").size());
        assertEquals(1, response.getGroupedRecords().get("Marketing").size());
    }

    @Test
    void queryDataset_shouldSortNumericAscending() throws Exception {
        List<DatasetRecordEntity> records = List.of(
                record("employee_dataset", "{\"id\":1,\"name\":\"John Doe\",\"age\":30,\"department\":\"Engineering\"}"),
                record("employee_dataset", "{\"id\":2,\"name\":\"Jane Smith\",\"age\":25,\"department\":\"Engineering\"}"),
                record("employee_dataset", "{\"id\":3,\"name\":\"Alice Brown\",\"age\":28,\"department\":\"Marketing\"}")
        );
        when(repository.findByDatasetNameOrderByIdAsc("employee_dataset")).thenReturn(records);

        DatasetQueryResponse response = service.queryDataset("employee_dataset", null, "age", "asc");

        assertEquals("asc", response.getOrder());
        assertEquals(25, response.getSortedRecords().getFirst().get("age").asInt());
        assertEquals(30, response.getSortedRecords().getLast().get("age").asInt());
    }

    @Test
    void queryDataset_shouldSortNumericDescending() throws Exception {
        List<DatasetRecordEntity> records = List.of(
                record("employee_dataset", "{\"id\":1,\"name\":\"John Doe\",\"age\":30,\"department\":\"Engineering\"}"),
                record("employee_dataset", "{\"id\":2,\"name\":\"Jane Smith\",\"age\":25,\"department\":\"Engineering\"}"),
                record("employee_dataset", "{\"id\":3,\"name\":\"Alice Brown\",\"age\":28,\"department\":\"Marketing\"}")
        );
        when(repository.findByDatasetNameOrderByIdAsc("employee_dataset")).thenReturn(records);

        DatasetQueryResponse response = service.queryDataset("employee_dataset", null, "age", "desc");

        assertEquals("desc", response.getOrder());
        assertEquals(30, response.getSortedRecords().getFirst().get("age").asInt());
        assertEquals(25, response.getSortedRecords().getLast().get("age").asInt());
    }

    @Test
    void queryDataset_shouldGroupAndSortTogether() throws Exception {
        List<DatasetRecordEntity> records = List.of(
                record("employee_dataset", "{\"id\":1,\"name\":\"John Doe\",\"age\":30,\"department\":\"Engineering\"}"),
                record("employee_dataset", "{\"id\":2,\"name\":\"Jane Smith\",\"age\":25,\"department\":\"Engineering\"}"),
                record("employee_dataset", "{\"id\":3,\"name\":\"Alice Brown\",\"age\":28,\"department\":\"Marketing\"}")
        );
        when(repository.findByDatasetNameOrderByIdAsc("employee_dataset")).thenReturn(records);

        DatasetQueryResponse response = service.queryDataset("employee_dataset", "department", "age", "asc");

        assertEquals(2, response.getGroupedRecords().get("Engineering").size());
        assertEquals(25, response.getGroupedRecords().get("Engineering").getFirst().get("age").asInt());
    }

    @Test
    void queryDataset_shouldHandleEmptyDataset() {
        when(repository.findByDatasetNameOrderByIdAsc("empty_dataset")).thenReturn(List.of());

        DatasetQueryResponse response = service.queryDataset("empty_dataset", null, null, "asc");

        assertEquals(0, response.getCount());
        assertNotNull(response.getSortedRecords());
    }

    @Test
    void addRecord_shouldRejectInvalidDatasetName() {
        assertThrows(InvalidDatasetNameException.class, () -> service.addRecord("bad dataset", objectMapper.readTree("{\"name\":\"John\"}")));
    }

    @Test
    void queryDataset_shouldRejectInvalidOrder() {
        assertThrows(InvalidQueryParameterException.class, () -> service.queryDataset("employee_dataset", null, null, "sideways"));
    }

    @Test
    void queryDataset_shouldRejectUnknownGroupingField() throws Exception {
        List<DatasetRecordEntity> records = List.of(
                record("employee_dataset", "{\"id\":1,\"name\":\"John\"}"),
                record("employee_dataset", "{\"id\":2,\"name\":\"Jane\"}")
        );
        when(repository.findByDatasetNameOrderByIdAsc("employee_dataset")).thenReturn(records);

        assertThrows(InvalidQueryParameterException.class, () -> service.queryDataset("employee_dataset", "department", null, "asc"));
    }

    @Test
    void queryDataset_shouldRejectUnknownSortingField() throws Exception {
        List<DatasetRecordEntity> records = List.of(
                record("employee_dataset", "{\"id\":1,\"name\":\"John\"}"),
                record("employee_dataset", "{\"id\":2,\"name\":\"Jane\"}")
        );
        when(repository.findByDatasetNameOrderByIdAsc("employee_dataset")).thenReturn(records);

        assertThrows(InvalidQueryParameterException.class, () -> service.queryDataset("employee_dataset", null, "department", "asc"));
    }

    private DatasetRecordEntity record(String datasetName, String json) throws Exception {
        DatasetRecordEntity entity = new DatasetRecordEntity(datasetName, objectMapper.readTree(json));
        Field idField = DatasetRecordEntity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(entity, System.nanoTime());
        return entity;
    }
}
