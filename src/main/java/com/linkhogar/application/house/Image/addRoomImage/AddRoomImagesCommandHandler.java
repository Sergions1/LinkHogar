package com.linkhogar.application.house.Image.addRoomImage;

import com.linkhogar.domain.house.House;
import com.linkhogar.domain.house.HouseRepository;
import com.linkhogar.domain.room.Room;
import com.linkhogar.infrastructure.externalServices.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddRoomImagesCommandHandler {
    private final HouseRepository houseRepository;
    private final CloudinaryService cloudinaryService;

    @Transactional
    public void handle(AddRoomImagesCommand command) {
        House house = houseRepository.getById(command.houseId());
        if (house == null) {
            throw new RuntimeException("Casa no encontrada con ID: " + command.houseId());
        }

        Room targetRoom = house.getRoomList().stream()
                .filter(r -> r.getId().equals(command.roomId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada en esta casa"));

        for (var file : command.files()) {
            try {
                String imageUrl = cloudinaryService.uploadImage(file);
                targetRoom.addPhoto(imageUrl);
            } catch (Exception e) {
                e.fillInStackTrace();
                throw new RuntimeException("Error al subir la imagen a Cloudinary", e);
            }
        }

        houseRepository.save(house);
    }
}
