package sk.cyrilgavala.wardrobeapi.auth.domain.exception;

public class InvalidCredentialsException extends RuntimeException {

  public InvalidCredentialsException(String message) {
    super(message);
  }

  public static InvalidCredentialsException create() {
    return new InvalidCredentialsException("Invalid username or password");
  }
}
