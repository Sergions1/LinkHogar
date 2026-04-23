package com.linkhogar.infrastructure.persistence.house;

import com.linkhogar.domain.house.HouseReport;
import com.linkhogar.domain.house.enums.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaHouseReportRepository extends JpaRepository<HouseReport, UUID> {
    List<HouseReport> findByStatus(ReportStatus status);

    Page<HouseReport> findAllByStatus(ReportStatus status, Pageable pageable);
}

