package com.linkhogar.application.house.SetHouseStatus;

import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.house.House;
import com.linkhogar.domain.house.HouseErrors;
import com.linkhogar.domain.house.HouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SetHouseStatusCommandHandler {
    private final HouseRepository houseRepository;

    public Result<Void> handle(SetHouseStatusCommand command) {

        System.out.println("Command de setHouseStatusCommandHandler");
        System.out.println(command.status());
        var house = houseRepository.getById(command.houseId());

        if (house == null) {
            return Result.failure(HouseErrors.NotFound(command.houseId()));
        }

        house.setPublicationStatus(command.status());
        houseRepository.save(house);

        System.out.println("Resultado de setHouseStatusCommandHandler");
        System.out.println(house.getPublicationStatus());

        return Result.success(null);
    }
}
