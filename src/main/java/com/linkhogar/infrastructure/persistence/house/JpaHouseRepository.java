package com.linkhogar.infrastructure.persistence.house;

import com.linkhogar.domain.common.enums.PublicationStatus;
import com.linkhogar.domain.house.House;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaHouseRepository extends JpaRepository<House, UUID> {
    Page<House> findByAddress_City(String City, Pageable pageable);
    long countByPublicationStatus(PublicationStatus status);
    Page<House> findByPublicationStatus(PublicationStatus status, Pageable pageable);
}
