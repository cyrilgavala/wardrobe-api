package sk.cyrilgavala.wardrobeapi.image.domain.exception;

public class ImageNotFoundException extends RuntimeException {

  public ImageNotFoundException(String message) {
    super(message);
  }

  public static ImageNotFoundException withId(String id) {
    return new ImageNotFoundException("Image not found with id: " + id);
  }
}