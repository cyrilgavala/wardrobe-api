package sk.cyrilgavala.wardrobeapi.web.advise;

import jakarta.validation.ConstraintViolationException;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import sk.cyrilgavala.wardrobeapi.exception.DuplicateUserException;
import sk.cyrilgavala.wardrobeapi.exception.InvalidCredentialsException;
import sk.cyrilgavala.wardrobeapi.exception.UserNotFoundException;
import sk.cyrilgavala.wardrobeapi.web.dto.ErrorResponse;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolationException(
      ConstraintViolationException ex, WebRequest request) {
    log.warn("Constraint violation: {}", ex.getMessage());
    ErrorResponse error = new ErrorResponse(
        HttpStatus.BAD_REQUEST.value(),
        "Validation Error",
        ex.getMessage(),
        request.getDescription(false).replace("uri=", "")
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException ex, WebRequest request) {
    log.warn("Validation failed: {}", ex.getMessage());
    String message = ex.getBindingResult().getFieldErrors().stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .reduce((a, b) -> a + "; " + b)
        .orElse("Validation failed");

    ErrorResponse error = new ErrorResponse(
        HttpStatus.BAD_REQUEST.value(),
        "Validation Error",
        message,
        request.getDescription(false).replace("uri=", "")
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(DuplicateUserException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateUserException(
      DuplicateUserException ex, WebRequest request) {
    log.warn("Duplicate user: {}", ex.getMessage());
    ErrorResponse error = new ErrorResponse(
        HttpStatus.CONFLICT.value(),
        "Conflict",
        ex.getMessage(),
        request.getDescription(false).replace("uri=", "")
    );
    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }

  @ExceptionHandler({NoSuchElementException.class, UserNotFoundException.class})
  public ResponseEntity<ErrorResponse> handleNotFoundException(
      RuntimeException ex, WebRequest request) {
    log.warn("Resource not found: {}", ex.getMessage());
    ErrorResponse error = new ErrorResponse(
        HttpStatus.NOT_FOUND.value(),
        "Not Found",
        ex.getMessage(),
        request.getDescription(false).replace("uri=", "")
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler({BadCredentialsException.class, InvalidCredentialsException.class})
  public ResponseEntity<ErrorResponse> handleAuthenticationException(
      RuntimeException ex, WebRequest request) {
    log.warn("Authentication failed: {}", ex.getMessage());
    ErrorResponse error = new ErrorResponse(
        HttpStatus.UNAUTHORIZED.value(),
        "Unauthorized",
        "Invalid username or password",
        request.getDescription(false).replace("uri=", "")
    );
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGlobalException(
      Exception ex, WebRequest request) {
    log.error("Unexpected error occurred", ex);
    ErrorResponse error = new ErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "Internal Server Error",
        "An unexpected error occurred. Please try again later.",
        request.getDescription(false).replace("uri=", "")
    );
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }
}
