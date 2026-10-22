package com.example.datasetapi.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(
        name = "dataset_records",
        indexes = {
                @Index(name = "idx_dataset_records_dataset_name", columnList = "dataset_name"),
                @Index(name = "idx_dataset_records_dataset_id", columnList = "dataset_name, id")
        }
)
public class DatasetRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dataset_name", nullable = false, length = 100)
    private String datasetName;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "record_data", nullable = false, columnDefinition = "json")
    private JsonNode recordData;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    protected DatasetRecordEntity() {
    }

    public DatasetRecordEntity(String datasetName, JsonNode recordData) {
        this.datasetName = datasetName;
        this.recordData = recordData;
    }

    public Long getId() {
        return id;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public JsonNode getRecordData() {
        return recordData;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
