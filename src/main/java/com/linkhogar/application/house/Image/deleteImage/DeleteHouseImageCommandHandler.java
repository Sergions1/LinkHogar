package com.linkhogar.application.house.Image.deleteImage;

import com.linkhogar.domain.common.result.Error;
import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.house.House;
import com.linkhogar.domain.house.HouseErrors;
import com.linkhogar.domain.house.HouseRepository;
import com.linkhogar.domain.user.UserErrors;
import com.linkhogar.infrastructure.externalServices.CloudinaryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteHouseImageCommandHandler {
    private final HouseRepository houseRepository;
    private final CloudinaryService cloudinaryService;

    @Transactional
    public Result<Void> handle(DeleteHouseImageCommand command) {
        House house = houseRepository.getById(command.houseId());
        if (house == null) return Result.failure(HouseErrors.NotFound());

        boolean isAdmin = command.authorities().stream().anyMatch(a -> a.getAuthority().equals("Admin"));
        if (!isAdmin && !house.getOwner().getId().equals(command.userId())) {
            return Result.failure(UserErrors.UNAUTHORIZED);
        }

        // 1. Verificar que la imagen pertenece a esta casa
        if (!house.getImages().contains(command.imageUrl())) {
            return Result.failure(Error.failure("401", "La imagen no pertenede a este hogar"));
        }

        // 2. Borrar de Cloudinary
        try {
            String publicId = cloudinaryService.extractPublicId(command.imageUrl());
            cloudinaryService.deleteImage(publicId);
        } catch (Exception e) {
            System.err.println("Error borrando de Cloudinary: " + e.getMessage());

        }

        // 3. Borrar de la Base de Datos
        house.getImages().remove(command.imageUrl());
        houseRepository.save(house);

        return Result.success(null);
    }
}
