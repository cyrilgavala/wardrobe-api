package sk.cyrilgavala.wardrobeapi.exception;

public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(String username) {
    super("User not found: " + username);
  }
}