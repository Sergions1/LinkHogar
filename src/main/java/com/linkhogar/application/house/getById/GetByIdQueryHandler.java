package com.linkhogar.application.house.getById;

import com.linkhogar.application.house.get.HouseResponse;
import com.linkhogar.domain.house.HouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetByIdQueryHandler {
    private final HouseRepository houseRepository;

    public HouseResponse handle(GetByIdQuery query){
        UUID houseId = UUID.fromString(query.houseId());

        return HouseResponse.mapToResponse(houseRepository.getById(houseId));
    }
}
