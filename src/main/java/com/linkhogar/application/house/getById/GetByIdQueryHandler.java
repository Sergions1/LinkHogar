package com.linkhogar.application.house.getById;

import com.linkhogar.application.house.get.HouseResponse;
import com.linkhogar.domain.common.enums.PublicationStatus;
import com.linkhogar.domain.house.House;
import com.linkhogar.domain.house.HouseRepository;
import com.linkhogar.domain.user.User;
import com.linkhogar.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetByIdQueryHandler {
    private final HouseRepository houseRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public HouseResponse handle(GetByIdQuery query){
        UUID houseId = UUID.fromString(query.houseId());

        return HouseResponse.mapToResponse(houseRepository.getById(houseId));
    }
}
