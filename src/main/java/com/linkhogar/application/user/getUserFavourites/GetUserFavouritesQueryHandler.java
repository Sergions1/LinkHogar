package com.linkhogar.application.user.getUserFavourites;

import com.linkhogar.domain.user.HouseFavourite;
import com.linkhogar.domain.user.HouseFavouriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetUserFavouritesQueryHandler {
    private final HouseFavouriteRepository houseFavouriteRepository;

    public List<UUID> handle(GetUserFavouriteQuery query){
        List<HouseFavourite> favs = houseFavouriteRepository.getUserFavourites(query.userId());
        List<UUID> housesFavsId = new ArrayList<UUID>();
        for (HouseFavourite fav : favs) {
            housesFavsId.add(fav.getHouse().getId());
        }

        return housesFavsId;
    }
}
