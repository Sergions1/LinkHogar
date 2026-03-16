package com.linkhogar.application.house.Image.addImage;

import com.linkhogar.domain.house.House;
import com.linkhogar.domain.house.HouseRepository;
import com.linkhogar.infrastructure.externalServices.CloudinaryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddHouseImagesCommandHandler {
    private final HouseRepository houseRepository;
    private final CloudinaryService cloudinaryService;

    @Transactional
    public void handle(AddHouseImagesCommand command) {
        House house = houseRepository.getById(command.houseId());

        if (house == null){
            throw new RuntimeException("Casa no encontrada con ID: " + command.houseId());
        }

        for (var file : command.files()) {
            try {
                String imageUrl = cloudinaryService.uploadImage(file);
                house.addImage(imageUrl);
            } catch (Exception e) {
                throw new RuntimeException("Error al subir la imagen a Cloudinary", e);
            }
        }

        houseRepository.save(house);
    }
}
