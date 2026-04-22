package com.linkhogar.infrastructure.persistence.house;

import com.linkhogar.domain.common.enums.PublicationStatus;
import com.linkhogar.domain.house.House;
import com.linkhogar.domain.house.HouseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class HouseRepositoryImpl implements HouseRepository {
    private final JpaHouseRepository jpaHouseRepository;

    @Override
    public void save(House house) {
        jpaHouseRepository.save(house);
    }

    @Override
    public Page<House> findByCity(String city, Pageable pageable) {
        return jpaHouseRepository.findByAddress_CityAndPublicationStatus(city, PublicationStatus.PUBLISHED ,pageable);
    }

    @Override
    public Page<House> getAll(Pageable pageable) {
        return jpaHouseRepository.findAll(pageable);
    }

    @Override
    public House getById(UUID houseId) {
        return jpaHouseRepository.findById(houseId)
                .orElseThrow(() -> new EntityNotFoundException("No se encontro ninguna entidad `HOUSE` con el id " + houseId));
    }

    @Override
    public long countByPublicationStatus(PublicationStatus status) {
        return jpaHouseRepository.countByPublicationStatus(status);
    }

    @Override
    public Boolean delete(UUID houseID) {
        try {
            jpaHouseRepository.deleteById(houseID);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Page<House> findByPublicationStatus(PublicationStatus status, Pageable pageable) {
        return jpaHouseRepository.findByPublicationStatus(status, pageable);
    }

    @Override
    public Page<House> findByOwnerId(UUID ownerId, Pageable pageable) {
        return jpaHouseRepository.findByOwnerId(ownerId, pageable);
    }
}