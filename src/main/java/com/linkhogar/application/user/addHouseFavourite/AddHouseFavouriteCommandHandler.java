package com.linkhogar.application.user.addHouseFavourite;

import com.linkhogar.domain.common.result.Error;
import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.house.House;
import com.linkhogar.domain.house.HouseErrors;
import com.linkhogar.domain.house.HouseRepository;
import com.linkhogar.domain.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddHouseFavouriteCommandHandler {
    private final UserRepository userRepository;
    private final HouseRepository houseRepository;
    private final HouseFavouriteRepository houseFavouriteRepository;

    public Result<Void> handle(AddHouseFavouriteCommand command){
        House house = houseRepository.getById(command.houseId());
        User user = userRepository.userById(command.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(house == null){
            return Result.failure(HouseErrors.NotFound(command.houseId()));
        }

        if(houseFavouriteRepository.isFavourite(command.userId(), command.houseId())) {
            return Result.success(null);
        }

        HouseFavourite favourite = HouseFavourite.builder()
                .id(UUID.randomUUID())
                .user(user)
                .house(house)
                .build();

        houseFavouriteRepository.addFavourite(favourite);

        return Result.success(null);
    }
}
