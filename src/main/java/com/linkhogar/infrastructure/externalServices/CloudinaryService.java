package com.linkhogar.infrastructure.externalServices;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadImage(MultipartFile file) throws IOException {
        // Sube el archivo y te devuelve un Map con toda la info
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

        // Extraemos solo la URL segura (https)
        return uploadResult.get("secure_url").toString();
    }

    public boolean deleteImage(String publicId){
        try {
            // Cloudinary devuelve un Map. Si va bien, contiene { "result": "ok" }
            Map<?, ?> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            return "ok".equals(result.get("result"));
        } catch (IOException e) {
            System.err.println("Error de conexión al eliminar en Cloudinary (publicId: " + publicId + "): " + e.getMessage());
            return false;
        }
    }

    public String uploadAvatar(MultipartFile file) throws IOException {
        Map params = ObjectUtils.asMap(
                "folder", "avatars",
                "transformation", new Transformation<>()
                        .width(150)
                        .height(150)
                        .crop("fill")
                        .gravity("face")
                        .quality("auto")
                        .fetchFormat("auto")
        );
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
        return uploadResult.get("secure_url").toString();
    }
}