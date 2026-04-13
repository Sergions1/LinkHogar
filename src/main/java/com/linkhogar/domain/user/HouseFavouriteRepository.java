package com.linkhogar.domain.user;

import com.linkhogar.application.house.get.HouseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HouseFavouriteRepository {
    public void addFavourite(HouseFavourite favourite);
    public void deleteFavourite(UUID userId, UUID houseId);
    boolean isFavourite(UUID userId, UUID houseId);
    Optional<HouseFavourite> getFavourite(UUID userId, UUID houseId);
    List<HouseFavourite> getUserFavourites(UUID userId);
    Page<HouseFavourite> findByUserId(UUID userId, Pageable pageable);

}
