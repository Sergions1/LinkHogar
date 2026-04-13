package com.linkhogar.infrastructure.persistence.user;

import com.linkhogar.domain.user.HouseFavourite;
import com.linkhogar.domain.user.HouseFavouriteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class HouseFavouriteImpl implements HouseFavouriteRepository {
    private final JpaHouseFavouriteRepository jpaHouseFavouriteRepository;

    @Override
    public boolean isFavourite(UUID userId, UUID houseId) {
        return jpaHouseFavouriteRepository.existsByUserIdAndHouseId(userId, houseId);
    }

    @Override
    public Optional<HouseFavourite> getFavourite(UUID userId, UUID houseId) {
        return jpaHouseFavouriteRepository.findByUserIdAndHouseId(userId, houseId);
    }

    @Override
    public List<HouseFavourite> getUserFavourites(UUID userId) {
        return jpaHouseFavouriteRepository.findAllByUserId(userId);
    }

    @Override
    public void addFavourite(HouseFavourite favorite) {
        jpaHouseFavouriteRepository.save(favorite);
    }

    @Override
    @Transactional
    public void deleteFavourite(UUID userId, UUID houseId) {
        jpaHouseFavouriteRepository.deleteByUserIdAndHouseId(userId, houseId);
    }

    @Override
    public Page<HouseFavourite> findByUserId(UUID userId, Pageable pageable){
        return jpaHouseFavouriteRepository.findByUserId(userId, pageable);
    }
}
