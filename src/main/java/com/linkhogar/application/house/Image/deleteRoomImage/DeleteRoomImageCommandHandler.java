package com.linkhogar.application.house.Image.deleteRoomImage;

import com.linkhogar.domain.common.result.Error;
import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.house.House;
import com.linkhogar.domain.house.HouseErrors;
import com.linkhogar.domain.house.HouseRepository;
import com.linkhogar.domain.room.Room;
import com.linkhogar.domain.user.UserErrors;
import com.linkhogar.infrastructure.externalServices.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteRoomImageCommandHandler {
    private final HouseRepository houseRepository;
    private final CloudinaryService cloudinaryService;

    @Transactional
    public Result<Void> handle(DeleteRoomImageCommand command) {
        House house = houseRepository.getById(command.houseId());
        if (house == null) return Result.failure(HouseErrors.NotFound(command.houseId()));

        // Verificación de permisos (igual que en la casa)
        boolean isOwner = house.getOwner().getId().equals(command.userId());
        boolean isAdmin = command.authorities().stream().anyMatch(a -> a.getAuthority().equals("Admin"));
        if (!isOwner && !isAdmin) return Result.failure(UserErrors.UNAUTHORIZED);

        // Buscamos la habitación
        Room room = house.getRoomList().stream()
                .filter(r -> r.getId().equals(command.roomId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada"));

        // Eliminamos la URL de la colección
        boolean removed = room.getPhotoUrls().remove(command.imageUrl());

        // Borrar de Cloudinary
        try {
            String publicId = cloudinaryService.extractPublicId(command.imageUrl());
            cloudinaryService.deleteImage(publicId);
        } catch (Exception e) {
            System.err.println("Error borrando de Cloudinary: " + e.getMessage());

        }

        if (removed) {
            houseRepository.save(house);
            return Result.success(null);
        }

        return Result.failure(Error.failure("Image.NotFound", "La imagen no existe en esta habitación"));
    }
}