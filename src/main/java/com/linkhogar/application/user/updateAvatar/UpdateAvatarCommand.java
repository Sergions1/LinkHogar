package com.linkhogar.application.user.updateAvatar;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record UpdateAvatarCommand(UUID userId, MultipartFile file) {}
