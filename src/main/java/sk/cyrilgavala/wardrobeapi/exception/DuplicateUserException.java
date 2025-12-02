package sk.cyrilgavala.wardrobeapi.exception;

public class DuplicateUserException extends RuntimeException {

  public DuplicateUserException(String message) {
    super(message);
  }
}