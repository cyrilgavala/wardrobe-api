package sk.cyrilgavala.wardrobeapi.image.application.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {

  String storeImage(MultipartFile file);

  byte[] getImage(String imageId);

  String getContentType(String imageId);

  void deleteImage(String imageId);

  boolean imageExists(String imageId);
}