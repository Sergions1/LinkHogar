package com.linkhogar.domain.house;

import com.linkhogar.application.house.getByCity.HouseCardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HouseRepository {
    void save(House house);
    Page<House> findByCity(String city, Pageable pageable);
    Page<House> getAll(Pageable pageable);
}
