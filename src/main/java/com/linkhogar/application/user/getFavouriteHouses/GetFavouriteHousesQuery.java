package com.linkhogar.application.user.getFavouriteHouses;

import java.util.UUID;

public record GetFavouriteHousesQuery (
    UUID userId,
    int page,
    int size
){
}
