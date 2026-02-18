package sk.cyrilgavala.wardrobeapi.image.infrastructure.persistence;

import com.mongodb.client.gridfs.model.GridFSFile;
import java.io.IOException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sk.cyrilgavala.wardrobeapi.image.application.service.ImageStorageService;
import sk.cyrilgavala.wardrobeapi.image.domain.exception.ImageNotFoundException;
import sk.cyrilgavala.wardrobeapi.image.domain.exception.InvalidImageException;

@Slf4j
@Service
@RequiredArgsConstructor
public class GridFsImageStorageService implements ImageStorageService {

  private static final long MAX_FILE_SIZE = 20 * 1024 * 1024;
  private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
      "image/jpeg",
      "image/jpg",
      "image/png",
      "image/webp"
  );

  private final GridFsTemplate gridFsTemplate;

  @Override
  public String storeImage(MultipartFile file) {
    validateImage(file);

    try {
      ObjectId fileId = gridFsTemplate.store(
          file.getInputStream(),
          file.getOriginalFilename(),
          file.getContentType()
      );

      log.info("Stored image with ID: {}", fileId);
      return fileId.toString();
    } catch (IOException e) {
      log.error("Failed to store image: {}", e.getMessage(), e);
      throw new RuntimeException("Failed to store image", e);
    }
  }

  @Override
  public byte[] getImage(String imageId) {
    GridFSFile file = findFile(imageId);

    try {
      GridFsResource resource = gridFsTemplate.getResource(file);
      return resource.getInputStream().readAllBytes();
    } catch (IOException e) {
      log.error("Failed to read image with ID: {}", imageId, e);
      throw new RuntimeException("Failed to read image", e);
    }
  }

  @Override
  public String getContentType(String imageId) {
    GridFSFile file = findFile(imageId);
    return file.getMetadata() != null ? file.getMetadata().get("_contentType", String.class)
        : "application/octet-stream";
  }

  @Override
  public void deleteImage(String imageId) {
    if (imageId == null || imageId.isBlank()) {
      return;
    }

    try {
      gridFsTemplate.delete(Query.query(Criteria.where("_id").is(new ObjectId(imageId))));
      log.info("Deleted image with ID: {}", imageId);
    } catch (IllegalArgumentException e) {
      log.warn("Invalid image ID format: {}", imageId);
    }
  }

  @Override
  public boolean imageExists(String imageId) {
    if (imageId == null || imageId.isBlank()) {
      return false;
    }

    try {
      GridFSFile file = gridFsTemplate.findOne(
          Query.query(Criteria.where("_id").is(new ObjectId(imageId))));
      return !file.getFilename().isBlank();
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  private GridFSFile findFile(String imageId) {
    try {
      return gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(new ObjectId(imageId))));
    } catch (IllegalArgumentException e) {
      throw ImageNotFoundException.withId(imageId);
    }
  }

  private void validateImage(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new InvalidImageException("Image file is required");
    }

    if (file.getSize() > MAX_FILE_SIZE) {
      throw InvalidImageException.tooLarge(file.getSize(), MAX_FILE_SIZE);
    }

    String contentType = file.getContentType();
    if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
      throw InvalidImageException.invalidType(contentType);
    }
  }
}