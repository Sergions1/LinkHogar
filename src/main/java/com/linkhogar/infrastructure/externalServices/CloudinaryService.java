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
                        .width(130)
                        .height(130)
                        .crop("fill")
                        .gravity("face")
                        .quality("auto")
                        .fetchFormat("auto")
        );
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
        return uploadResult.get("secure_url").toString();
    }

    public String extractPublicId(String url) {
        try {
            // Separa la URL por la carpeta base de Cloudinary
            String[] parts = url.split("/upload/");
            if (parts.length != 2) return null;

            return getString(parts);// Resultado: "linkhogar_avatars/foto"

        } catch (Exception e) {
            System.err.println("Error extrayendo public_id de la URL: " + url);
            return null;
        }
    }

    private String getString(String[] parts) {
        String afterUpload = parts[1]; // ej: "v1623456789/linkhogar_avatars/foto.jpg"

        // Elimina el tag de versión (v123456789/) si existe
        if (afterUpload.matches("^v\\d+/.*")) {
            afterUpload = afterUpload.substring(afterUpload.indexOf("/") + 1);
        }

        // Elimina la extensión del archivo (.jpg, .png, etc.)
        int lastDotIndex = afterUpload.lastIndexOf('.');
        if (lastDotIndex != -1) {
            afterUpload = afterUpload.substring(0, lastDotIndex);
        }
        return afterUpload;
    }

}