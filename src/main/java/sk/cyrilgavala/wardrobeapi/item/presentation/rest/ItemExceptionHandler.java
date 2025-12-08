package sk.cyrilgavala.wardrobeapi.item.presentation.rest;

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
import org.springframework.web.context.request.WebRequest;
import sk.cyrilgavala.wardrobeapi.item.domain.exception.ItemAccessDeniedException;
import sk.cyrilgavala.wardrobeapi.item.domain.exception.ItemNotFoundException;

@Slf4j
@RestControllerAdvice(assignableTypes = ItemController.class)
public class ItemExceptionHandler {

  @ExceptionHandler(ItemNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleItemNotFoundException(
      ItemNotFoundException ex,
      WebRequest request) {
    log.error("Item not found: {}", ex.getMessage());

    ErrorResponse error = ErrorResponse.of(
        HttpStatus.NOT_FOUND.value(),
        "Item Not Found",
        ex.getMessage(),
        Instant.now(),
        request.getDescription(false)
    );

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(ItemAccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleItemAccessDeniedException(
      ItemAccessDeniedException ex,
      WebRequest request) {
    log.error("Item access denied: {}", ex.getMessage());

    ErrorResponse error = ErrorResponse.of(
        HttpStatus.FORBIDDEN.value(),
        "Access Denied",
        ex.getMessage(),
        Instant.now(),
        request.getDescription(false)
    );

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(
      MethodArgumentNotValidException ex,
      WebRequest request) {
    log.error("Validation failed: {}", ex.getMessage());

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    ValidationErrorResponse error = ValidationErrorResponse.of(
        HttpStatus.BAD_REQUEST.value(),
        "Validation Failed",
        "Request validation failed",
        Instant.now(),
        request.getDescription(false),
        errors
    );

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
      IllegalArgumentException ex,
      WebRequest request) {
    log.error("Illegal argument: {}", ex.getMessage());

    ErrorResponse error = ErrorResponse.of(
        HttpStatus.BAD_REQUEST.value(),
        "Bad Request",
        ex.getMessage(),
        Instant.now(),
        request.getDescription(false)
    );

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  public record ErrorResponse(
      int status,
      String error,
      String message,
      Instant timestamp,
      String path
  ) {

    public static ErrorResponse of(int status, String error, String message, Instant timestamp,
        String path) {
      return new ErrorResponse(status, error, message, timestamp, path);
    }
  }

  public record ValidationErrorResponse(
      int status,
      String error,
      String message,
      Instant timestamp,
      String path,
      Map<String, String> validationErrors
  ) {

    public static ValidationErrorResponse of(int status, String error, String message,
        Instant timestamp, String path, Map<String, String> validationErrors) {
      return new ValidationErrorResponse(status, error, message, timestamp, path,
          validationErrors);
    }
  }
}

