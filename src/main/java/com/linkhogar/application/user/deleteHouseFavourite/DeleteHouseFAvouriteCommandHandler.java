package com.linkhogar.application.user.deleteHouseFavourite;

import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.user.HouseFavourite;
import com.linkhogar.domain.user.HouseFavouriteErrors;
import com.linkhogar.domain.user.HouseFavouriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeleteHouseFAvouriteCommandHandler {
    private final HouseFavouriteRepository houseFavouriteRepository;

    public Result<Void> handler(DeleteHouseFavouriteCommand command){
        try {
            Optional<HouseFavourite> houseFavouriteOptional = houseFavouriteRepository.getFavourite(command.userId(), command.houseId());

            if(houseFavouriteOptional.isEmpty()){return Result.success(null);}

            // Asumiendo que tu comando tiene los ID necesarios
            houseFavouriteRepository.deleteFavourite(
                    command.userId(),
                    command.houseId()
            );

            return Result.success(null);

        } catch (Exception e) {
            return Result.failure(HouseFavouriteErrors.notFound(command.userId(), command.houseId()));
        }
    }
}
