package com.student_loan.utils;

import java.io.IOException;
import java.nio.file.*;
import java.util.Base64;
import java.util.UUID;

public class ImageUtil {

    public static String saveBase64Image(String base64Image, String uploadDir) throws IOException {
        // Elimina encabezado tipo data:image/png;base64,...
        String[] parts = base64Image.split(",");
        String imageData = parts.length > 1 ? parts[1] : parts[0];

        byte[] imageBytes = Base64.getDecoder().decode(imageData);

        String filename = UUID.randomUUID().toString() + ".png";
        Path imagePath = Paths.get(uploadDir, "images", filename);

        Files.createDirectories(imagePath.getParent()); // Asegura que la carpeta exista
        Files.write(imagePath, imageBytes);

        return "/images/" + filename; // Ruta accesible desde frontend
    }
}
