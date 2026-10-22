package com.example.datasetapi.repository;

import com.example.datasetapi.entity.DatasetRecordEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DatasetRecordRepository extends JpaRepository<DatasetRecordEntity, Long> {

    List<DatasetRecordEntity> findByDatasetNameOrderByIdAsc(String datasetName);
}
