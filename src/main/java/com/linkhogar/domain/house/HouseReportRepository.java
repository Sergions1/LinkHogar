package com.linkhogar.domain.house;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;


public interface HouseReportRepository {
    void save(HouseReport report);
    HouseReport findById(UUID id);
    Page<HouseReport> getAll(Pageable pageable);
    void deleteById(UUID id);
    Page<HouseReport> getPendant(Pageable pageable);
    long countByPendant();
}
