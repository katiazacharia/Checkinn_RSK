package com.project.checkinn;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Service
public class ImageStorageService {

    private final String BASE_DIR = "src/Uploads/";

    public String saveHotelImage(MultipartFile file) {
        return saveFile(file, "hotels");
    }

    public String saveRoomImage(MultipartFile file) {
        return saveFile(file, "rooms");
    }

    private String saveFile(MultipartFile file, String folder) {
        try {
            if (file == null || file.isEmpty()) {
                throw new RuntimeException("File is empty");
            }

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            Path uploadPath = Paths.get(BASE_DIR + folder);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "/Uploads/" + folder + "/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
}