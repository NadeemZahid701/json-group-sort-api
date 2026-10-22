package com.example.datasetapi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.datasetapi.dto.DatasetQueryResponse;
import com.example.datasetapi.dto.RecordCreationResponse;
import com.example.datasetapi.entity.DatasetRecordEntity;
import com.example.datasetapi.exception.ApiExceptionHandler;
import com.example.datasetapi.repository.DatasetRecordRepository;
import com.example.datasetapi.service.DatasetRecordService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class DatasetControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private DatasetRecordRepository repository;

    private DatasetRecordService datasetRecordService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        datasetRecordService = new DatasetRecordService(repository);
        mockMvc = MockMvcBuilders.standaloneSetup(new DatasetController(datasetRecordService), new RootController())
                .setControllerAdvice(new ApiExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    @Test
    void createRecord_shouldReturnCreatedStatus() throws Exception {
        when(repository.save(any(DatasetRecordEntity.class))).thenAnswer(invocation -> {
            DatasetRecordEntity entity = invocation.getArgument(0);
            Field idField = DatasetRecordEntity.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, 1L);
            return entity;
        });

        mockMvc.perform(post("/api/dataset/employee_dataset/record")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"John Doe\",\"age\":30,\"department\":\"Engineering\"}"))
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Record added successfully"))
                .andExpect(jsonPath("$.recordId").value(1));
    }

    @Test
    void queryDataset_shouldReturnGroupedRecords() throws Exception {
        LinkedHashMap<String, List<JsonNode>> grouped = new LinkedHashMap<>();
        grouped.put("Engineering", List.of(
                objectMapper.readTree("{\"id\":1,\"name\":\"John Doe\",\"age\":30,\"department\":\"Engineering\"}"),
                objectMapper.readTree("{\"id\":2,\"name\":\"Jane Smith\",\"age\":25,\"department\":\"Engineering\"}")
        ));
        grouped.put("Marketing", List.of(
                objectMapper.readTree("{\"id\":3,\"name\":\"Alice Brown\",\"age\":28,\"department\":\"Marketing\"}")
        ));

        when(repository.findByDatasetNameOrderByIdAsc("employee_dataset")).thenReturn(List.of(
                record("employee_dataset", "{\"id\":1,\"name\":\"John Doe\",\"age\":30,\"department\":\"Engineering\"}"),
                record("employee_dataset", "{\"id\":2,\"name\":\"Jane Smith\",\"age\":25,\"department\":\"Engineering\"}"),
                record("employee_dataset", "{\"id\":3,\"name\":\"Alice Brown\",\"age\":28,\"department\":\"Marketing\"}")
        ));

        mockMvc.perform(get("/api/dataset/employee_dataset/query").param("groupBy", "department"))
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dataset").value("employee_dataset"))
                .andExpect(jsonPath("$.groupedRecords.Engineering[0].name").value("John Doe"));
    }

    @Test
    void queryDataset_shouldRejectUnknownSortDirection() throws Exception {
        mockMvc.perform(get("/api/dataset/employee_dataset/query").param("sortBy", "age").param("order", "sideways"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rootEndpoint_shouldExposeDocumentationAndHealthLinks() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.service").value("JSON Dataset Group By and Sort By API"));
    }

    private DatasetRecordEntity record(String datasetName, String json) throws Exception {
        DatasetRecordEntity entity = new DatasetRecordEntity(datasetName, objectMapper.readTree(json));
        Field idField = DatasetRecordEntity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(entity, System.nanoTime());
        return entity;
    }
}
