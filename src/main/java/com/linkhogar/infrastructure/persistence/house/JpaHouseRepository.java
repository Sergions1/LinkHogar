package com.linkhogar.infrastructure.persistence.house;

import com.linkhogar.domain.house.House;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaHouseRepository extends JpaRepository<House, UUID> {

}
