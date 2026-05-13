package com.linkhogar.application.house.Image.addRoomImage;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public record AddRoomImagesCommand(
        UUID roomId,
        UUID houseId,
        List<MultipartFile> files
) {
}
