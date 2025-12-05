package sk.cyrilgavala.wardrobeapi.item.domain.exception;

public class ItemNotFoundException extends RuntimeException {

  public ItemNotFoundException(String message) {
    super(message);
  }

  public static ItemNotFoundException withId(String id) {
    return new ItemNotFoundException("Item not found with id: " + id);
  }
}

