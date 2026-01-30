package com.linkhogar.infrastructure.persistence.house;

import com.linkhogar.domain.house.House;
import com.linkhogar.domain.house.HouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HouseRepositoryImpl implements HouseRepository {
    private final JpaHouseRepository jpaHouseRepository;

    @Override
    public void save(House house) {
        jpaHouseRepository.save(house);
    }
}
