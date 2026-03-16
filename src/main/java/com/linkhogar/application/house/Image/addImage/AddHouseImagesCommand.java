package com.linkhogar.application.house.Image.addImage;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public record AddHouseImagesCommand (UUID houseId, List<MultipartFile> files) {}
