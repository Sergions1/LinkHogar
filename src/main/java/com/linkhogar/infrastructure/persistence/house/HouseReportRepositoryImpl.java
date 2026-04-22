package com.linkhogar.infrastructure.persistence.house;

import com.linkhogar.domain.house.HouseReport;
import com.linkhogar.domain.house.HouseReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class HouseReportRepositoryImpl implements HouseReportRepository {
    private final JpaHouseReportRepository jpaHouseReportRepository;


    @Override
    public void save(HouseReport report) {
        if(report != null) {
            jpaHouseReportRepository.save(report);
        }
    }

    @Override
    public HouseReport findById(UUID id) {
      return jpaHouseReportRepository.findById(id).orElse(null);
    }

    @Override
    public Page<HouseReport> getAll(Pageable pageable){
        return jpaHouseReportRepository.findAll(pageable);
    }

    @Override
    public void deleteById(UUID id) {
        jpaHouseReportRepository.deleteById(id);
    }
}
