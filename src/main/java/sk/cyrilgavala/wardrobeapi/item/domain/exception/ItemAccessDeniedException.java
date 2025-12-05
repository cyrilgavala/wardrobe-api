package sk.cyrilgavala.wardrobeapi.item.domain.exception;

public class ItemAccessDeniedException extends RuntimeException {

  public ItemAccessDeniedException(String message) {
    super(message);
  }

  public static ItemAccessDeniedException withId(String id) {
    return new ItemAccessDeniedException("Access denied to item with id: " + id);
  }
}

