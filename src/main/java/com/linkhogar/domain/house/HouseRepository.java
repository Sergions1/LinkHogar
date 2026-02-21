package com.linkhogar.domain.house;

import com.linkhogar.application.house.getByCity.HouseCardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface HouseRepository {
    void save(House house);
    Page<House> findByCity(String city, Pageable pageable);
    Page<House> getAll(Pageable pageable);
    House getById(UUID houseId);
}
