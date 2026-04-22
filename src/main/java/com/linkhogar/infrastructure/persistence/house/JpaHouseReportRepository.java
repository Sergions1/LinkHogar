package com.linkhogar.infrastructure.persistence.house;

import com.linkhogar.domain.house.HouseReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaHouseReportRepository extends JpaRepository<HouseReport, UUID> {
}

