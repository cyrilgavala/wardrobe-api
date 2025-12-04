package sk.cyrilgavala.wardrobeapi.auth.domain.exception;

public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(String message) {
    super(message);
  }

  public static UserNotFoundException withUsername(String username) {
    return new UserNotFoundException("User with username %s not found".formatted(username));
  }
}
