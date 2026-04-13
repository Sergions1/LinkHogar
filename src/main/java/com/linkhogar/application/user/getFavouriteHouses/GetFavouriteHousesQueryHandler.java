package com.linkhogar.application.user.getFavouriteHouses;

import com.linkhogar.application.house.getByCity.HouseCardResponse;
import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.house.House;
import com.linkhogar.domain.house.HouseRepository;
import com.linkhogar.domain.user.HouseFavourite;
import com.linkhogar.domain.user.HouseFavouriteErrors;
import com.linkhogar.domain.user.HouseFavouriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetFavouriteHousesQueryHandler {
    private final HouseRepository houseRepository;
    private final HouseFavouriteRepository houseFavouriteRepository;

    public Result<Page<HouseCardResponse>> handle(GetFavouriteHousesQuery query) {
        try{
            Pageable pageable = PageRequest.of(query.page(), query.size(), Sort.by(Sort.Direction.DESC, "addedAt"));
            Page<HouseFavourite> favouritesPage = houseFavouriteRepository.findByUserId(query.userId(), pageable);

            if (favouritesPage.isEmpty()) {
                return Result.success(Page.empty(pageable));
            }

            List<House> houses = favouritesPage.getContent().stream()
                    .map(HouseFavourite::getHouse) // Nos quedamos solo con el UUID
                    .toList();

            List<HouseCardResponse> houseCards = houses.stream()
                    .map(HouseCardResponse::toHouseCardResponse)
                    .toList();

            Page<HouseCardResponse> resultPage = new PageImpl<>(houseCards, pageable, favouritesPage.getTotalElements());

            return Result.success(resultPage);
        }catch(Exception ex){
            return Result.failure(HouseFavouriteErrors.notExist());
        }
    }
}
