package sk.cyrilgavala.wardrobeapi.auth.domain.exception;

public class DuplicateUserException extends RuntimeException {

  public DuplicateUserException(String message) {
    super(message);
  }

  public static DuplicateUserException withUsername(String username) {
    return new DuplicateUserException("User with username %s already exists".formatted(username));
  }

  public static DuplicateUserException withEmail(String email) {
    return new DuplicateUserException("User with email %s already exists".formatted(email));
  }
}
