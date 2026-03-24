package com.linkhogar.application.house.delete;

import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.house.*;
import com.linkhogar.infrastructure.externalServices.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
        Boolean deletedImages = true;
        List<String> images = house.getImages();
        if (images != null && !images.isEmpty()) {
            for (String imageUrl : images) {
                try {
                    String publicId = extractPublicIdFromUrl(imageUrl);
                    Boolean deleted = cloudinaryService.deleteImage(publicId);
                    if(!deleted){
                        deletedImages = false;
                    }
                } catch (Exception e) {
                    System.err.println("No se pudo eliminar la imagen de Cloudinary: " + imageUrl);
                }
            }
        }

        if (!deletedImages) {
            System.out.println("Error al eliminar las imágenes de Cloudinary.");
        }
        houseRepository.delete(house.getId());

        return Result.success(null);
    }

    private String extractPublicIdFromUrl(String imageUrl) {
        int lastSlashIndex = imageUrl.lastIndexOf("/");
        int lastDotIndex = imageUrl.lastIndexOf(".");

        if (lastSlashIndex != -1 && lastDotIndex != -1 && lastDotIndex > lastSlashIndex) {
            return imageUrl.substring(lastSlashIndex + 1, lastDotIndex);
        }
        return imageUrl;
    }
}
