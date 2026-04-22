package com.linkhogar.application.house.delete;

import com.linkhogar.domain.common.enums.PublicationStatus;
import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.house.*;
import com.linkhogar.domain.user.UserErrors;
import com.linkhogar.infrastructure.externalServices.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DeleteHouseCommandHandler {
    private final HouseRepository houseRepository;
    private final CloudinaryService cloudinaryService;

    public Result<Void> handle(DeleteHouseCommand command) {
        House house = houseRepository.getById(command.houseId());

        if (house == null) {
            return Result.failure(HouseErrors.NotFound(command.houseId()));
        }

        boolean isAdminOrStaff = command.authorities().stream()
                .anyMatch(auth -> Objects.equals(auth.getAuthority(), "Admin") || Objects.equals(auth.getAuthority(), "LinkHogar"));
        boolean isOwner = house.getOwner().getId().equals(command.userId());

        if (!isAdminOrStaff && !isOwner) {
            return Result.failure(UserErrors.UNAUTHORIZED);
        }

        house.setPublicationStatus(PublicationStatus.ARCHIVED);

        // 4. Guardamos los cambios
        houseRepository.save(house);

        return Result.success(null);

    }
}
