package sk.cyrilgavala.wardrobeapi.image.domain.exception;

public class InvalidImageException extends RuntimeException {

  public InvalidImageException(String message) {
    super(message);
  }

  public static InvalidImageException invalidType(String contentType) {
    return new InvalidImageException(
        "Invalid image type: " + contentType + ". Only JPEG, PNG, and WebP are allowed.");
  }

  public static InvalidImageException tooLarge(long size, long maxSize) {
    return new InvalidImageException(
        "Image size " + size + " bytes exceeds maximum allowed size of " + maxSize + " bytes.");
  }
}