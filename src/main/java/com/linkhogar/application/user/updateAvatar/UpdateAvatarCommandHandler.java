package com.linkhogar.application.user.updateAvatar;

import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.linkhogar.application.house.Image.addImage.AddHouseImagesCommand;
import com.linkhogar.domain.common.result.Error;
import com.linkhogar.domain.common.result.Result;
import com.linkhogar.domain.user.User;
import com.linkhogar.domain.user.UserErrors;
import com.linkhogar.domain.user.UserRepository;
import com.linkhogar.infrastructure.externalServices.CloudinaryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

import static com.linkhogar.domain.common.result.Result.failure;

@Service
@RequiredArgsConstructor
public class UpdateAvatarCommandHandler{
    private final CloudinaryService cloudinaryService;
    private final UserRepository userRepository;

    @Transactional
    public Result<Void> handle(UpdateAvatarCommand command) {
        var optionalUser = userRepository.userById(command.userId());
        if(optionalUser.isEmpty()){
            return failure(UserErrors.NotFound(command.userId()));
        }
        User user = optionalUser.get();

        if(user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()){
            String publicId = cloudinaryService.extractPublicId(user.getAvatarUrl());
            cloudinaryService.deleteImage(publicId);
        }

        String avatarUrl;
        try{
            avatarUrl = cloudinaryService.uploadAvatar(command.file());
        }catch (IOException e){
            System.out.println("Hubo un error al subir el avatar"+e.getMessage());
            return Result.failure(Error.failure("501", "Hubo un error al subir el avatar"));
        }

        user.setAvatarUrl(avatarUrl);
        userRepository.saveUser(user);

        return Result.success(null);
    }
}
