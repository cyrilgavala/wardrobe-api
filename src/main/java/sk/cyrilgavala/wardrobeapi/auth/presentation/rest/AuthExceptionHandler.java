package sk.cyrilgavala.wardrobeapi.auth.presentation.rest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sk.cyrilgavala.wardrobeapi.auth.domain.exception.DuplicateUserException;
import sk.cyrilgavala.wardrobeapi.auth.domain.exception.InvalidCredentialsException;
import sk.cyrilgavala.wardrobeapi.auth.domain.exception.UserNotFoundException;

@Slf4j
@RestControllerAdvice(basePackages = "sk.cyrilgavala.wardrobeapi.auth")
public class AuthExceptionHandler {

  @ExceptionHandler(DuplicateUserException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateUser(DuplicateUserException ex) {
    log.error("Duplicate user error: {}", ex.getMessage());
    ErrorResponse error = ErrorResponse.builder()
        .timestamp(Instant.now())
        .status(HttpStatus.CONFLICT.value())
        .error("Conflict")
        .message(ex.getMessage())
        .build();
    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
    log.error("User not found error: {}", ex.getMessage());
    ErrorResponse error = ErrorResponse.builder()
        .timestamp(Instant.now())
        .status(HttpStatus.NOT_FOUND.value())
        .error("Not Found")
        .message(ex.getMessage())
        .build();
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
    log.error("Invalid credentials error: {}", ex.getMessage());
    ErrorResponse error = ErrorResponse.builder()
        .timestamp(Instant.now())
        .status(HttpStatus.UNAUTHORIZED.value())
        .error("Unauthorized")
        .message(ex.getMessage())
        .build();
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ValidationErrorResponse> handleValidationErrors(
      MethodArgumentNotValidException ex) {
    log.error("Validation error: {}", ex.getMessage());

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    ValidationErrorResponse error = ValidationErrorResponse.builder()
        .timestamp(Instant.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Validation Failed")
        .message("Invalid input data")
        .errors(errors)
        .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericError(Exception ex) {
    log.error("Unexpected error occurred", ex);
    ErrorResponse error = ErrorResponse.builder()
        .timestamp(Instant.now())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .error("Internal Server Error")
        .message("An unexpected error occurred")
        .build();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }

  public record ErrorResponse(
      Instant timestamp,
      int status,
      String error,
      String message
  ) {

    public static ErrorResponseBuilder builder() {
      return new ErrorResponseBuilder();
    }

    public static class ErrorResponseBuilder {

      private Instant timestamp;
      private int status;
      private String error;
      private String message;

      public ErrorResponseBuilder timestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
      }

      public ErrorResponseBuilder status(int status) {
        this.status = status;
        return this;
      }

      public ErrorResponseBuilder error(String error) {
        this.error = error;
        return this;
      }

      public ErrorResponseBuilder message(String message) {
        this.message = message;
        return this;
      }

      public ErrorResponse build() {
        return new ErrorResponse(timestamp, status, error, message);
      }
    }
  }

  public record ValidationErrorResponse(
      Instant timestamp,
      int status,
      String error,
      String message,
      Map<String, String> errors
  ) {

    public static ValidationErrorResponseBuilder builder() {
      return new ValidationErrorResponseBuilder();
    }

    public static class ValidationErrorResponseBuilder {

      private Instant timestamp;
      private int status;
      private String error;
      private String message;
      private Map<String, String> errors;

      public ValidationErrorResponseBuilder timestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
      }

      public ValidationErrorResponseBuilder status(int status) {
        this.status = status;
        return this;
      }

      public ValidationErrorResponseBuilder error(String error) {
        this.error = error;
        return this;
      }

      public ValidationErrorResponseBuilder message(String message) {
        this.message = message;
        return this;
      }

      public ValidationErrorResponseBuilder errors(Map<String, String> errors) {
        this.errors = errors;
        return this;
      }

      public ValidationErrorResponse build() {
        return new ValidationErrorResponse(timestamp, status, error, message, errors);
      }
    }
  }
}

