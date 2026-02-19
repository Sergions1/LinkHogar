package com.linkhogar.infrastructure.persistence.house;

import com.linkhogar.domain.house.House;
import com.linkhogar.domain.house.HouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HouseRepositoryImpl implements HouseRepository {
    private final JpaHouseRepository jpaHouseRepository;

    @Override
    public void save(House house) {
        jpaHouseRepository.save(house);
    }

    @Override
    public List<House> findByCity(String city) {
        return jpaHouseRepository.findByAddress_City(city);
    }

    @Override
    public Page<House> getAll(Pageable pageable) {
        return jpaHouseRepository.findAll(pageable);
    }
}
