package com.linkhogar.infrastructure.persistence.user;

import com.linkhogar.domain.user.HouseFavourite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaHouseFavouriteRepository extends JpaRepository<HouseFavourite, UUID> {
    boolean existsByUserIdAndHouseId(UUID userId, UUID houseId);
    List<HouseFavourite> findAllByUserId(UUID userId);
    void deleteByUserIdAndHouseId(UUID userId, UUID houseId);
    Optional<HouseFavourite> findByUserIdAndHouseId(UUID userId, UUID houseId);
    Page<HouseFavourite> findByUserId(UUID userId, Pageable pageable);
}
