package com.example.datasetapi.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.datasetapi.entity.DatasetRecordEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DatasetRecordRepositoryIntegrationTest {

    @Autowired
    private DatasetRecordRepository repository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void save_shouldPersistJsonRecordWithH2() throws Exception {
        DatasetRecordEntity entity = new DatasetRecordEntity(
                "employee_dataset",
                objectMapper.readTree("{\"id\":1,\"name\":\"John Doe\",\"age\":30,\"department\":\"Engineering\"}")
        );

        DatasetRecordEntity saved = repository.saveAndFlush(entity);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getDatasetName()).isEqualTo("employee_dataset");
        assertThat(saved.getRecordData().get("name").asText()).isEqualTo("John Doe");
    }
}
