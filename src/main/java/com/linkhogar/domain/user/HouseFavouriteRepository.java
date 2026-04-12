package com.linkhogar.domain.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HouseFavouriteRepository {
    public void addFavourite(HouseFavourite favourite);
    public void deleteFavourite(UUID userId, UUID houseId);
    boolean isFavourite(UUID userId, UUID houseId);
    Optional<HouseFavourite> getFavourite(UUID userId, UUID houseId);
    List<HouseFavourite> getUserFavourites(UUID userId);
}
