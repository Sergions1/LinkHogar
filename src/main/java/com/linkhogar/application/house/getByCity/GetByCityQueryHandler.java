package com.linkhogar.application.house.getByCity;

import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.house.House;
import com.linkhogar.domain.house.HouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetByCityQueryHandler {
    private final HouseRepository houseRepository;

    public Page<HouseCardResponse> handle(GetByCityQuery query){
        Pageable pageable = PageRequest.of(query.page(), query.size());
        Page<House> housePage = houseRepository.findByCity(query.city(), pageable);

        return housePage.map(this::toHouseCardResponse);
    }


    //Mapeo de House -> HouseCardResponse
    private HouseCardResponse toHouseCardResponse(House house) {
        return new HouseCardResponse(
                house.getId().toString(),
                house.getTitle(),
                house.getDescription(),
                house.getPublicationDate(),
                house.getUpdateDate(),
                house.getHouseType(),
                house.getStatus(),
                house.getSize(),
                house.getRooms(),
                house.getBaths(),
                house.getPrice(),
                house.getAddress()
        );
    }
}
