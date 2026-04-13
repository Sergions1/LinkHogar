package com.linkhogar.application.user.deleteHouseFavourite;

import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.user.HouseFavouriteErrors;
import com.linkhogar.domain.user.HouseFavouriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteHouseFAvouriteCommandHandler {
    private final HouseFavouriteRepository houseFavouriteRepository;

    public Result<Void> handler(DeleteHouseFavouriteCommand command){
        try {
            // Asumiendo que tu comando tiene los ID necesarios
            houseFavouriteRepository.deleteFavourite(
                    command.userId(),
                    command.houseId()
            );

            // Retornamos éxito (ajusta al método exacto de tu clase Result)
            return Result.success(null);

        } catch (Exception e) {
            return Result.failure(HouseFavouriteErrors.notFound(command.userId(), command.houseId()));
        }
    }
}
