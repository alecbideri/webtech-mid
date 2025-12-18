package com.alec.FindJobApp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Service for handling file uploads.
 */
@Service
public class FileStorageService {

  private final Path uploadPath;

  public FileStorageService(@Value("${app.upload.dir}") String uploadDir) {
    this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
    try {
      Files.createDirectories(this.uploadPath);
    } catch (IOException e) {
      throw new RuntimeException("Could not create upload directory", e);
    }
  }

  /**
   * Stores a file and returns the file path.
   */
  public String storeFile(MultipartFile file, String subdirectory) {
    try {
      // Validate file
      if (file.isEmpty()) {
        throw new RuntimeException("Cannot store empty file");
      }

      // Create subdirectory if specified
      Path targetDir = subdirectory != null
          ? uploadPath.resolve(subdirectory)
          : uploadPath;
      Files.createDirectories(targetDir);

      // Generate unique filename
      String originalFilename = file.getOriginalFilename();
      String extension = originalFilename != null && originalFilename.contains(".")
          ? originalFilename.substring(originalFilename.lastIndexOf("."))
          : "";
      String newFilename = UUID.randomUUID().toString() + extension;

      // Store the file
      Path targetPath = targetDir.resolve(newFilename);
      Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

      return subdirectory != null
          ? subdirectory + "/" + newFilename
          : newFilename;

    } catch (IOException e) {
      throw new RuntimeException("Failed to store file: " + e.getMessage(), e);
    }
  }

  /**
   * Deletes a file.
   */
  public boolean deleteFile(String filePath) {
    try {
      Path file = uploadPath.resolve(filePath);
      return Files.deleteIfExists(file);
    } catch (IOException e) {
      return false;
    }
  }

  /**
   * Gets the full path to a file.
   */
  public Path getFilePath(String filePath) {
    return uploadPath.resolve(filePath);
  }
}
