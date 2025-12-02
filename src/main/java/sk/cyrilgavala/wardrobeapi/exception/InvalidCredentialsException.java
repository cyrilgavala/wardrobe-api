package sk.cyrilgavala.wardrobeapi.exception;

public class InvalidCredentialsException extends RuntimeException {

  public InvalidCredentialsException() {
    super("Invalid username or password");
  }
}

