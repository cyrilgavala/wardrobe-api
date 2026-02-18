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
import sk.cyrilgavala.wardrobeapi.shared.presentation.dto.ErrorResponse;
import sk.cyrilgavala.wardrobeapi.shared.presentation.dto.ValidationErrorResponse;

@Slf4j
@RestControllerAdvice(assignableTypes = ItemController.class)
public class ItemExceptionHandler {

  @ExceptionHandler(ItemNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleItemNotFoundException(
      ItemNotFoundException ex,
      WebRequest request) {
    log.error("Item not found: {}", ex.getMessage());

    ErrorResponse error = ErrorResponse.builder()
        .status(HttpStatus.NOT_FOUND.value())
        .error("Item Not Found")
        .message(ex.getMessage())
        .timestamp(Instant.now())
        .build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(ItemAccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleItemAccessDeniedException(
      ItemAccessDeniedException ex,
      WebRequest request) {
    log.error("Item access denied: {}", ex.getMessage());

    ErrorResponse error = ErrorResponse.builder()
        .status(HttpStatus.FORBIDDEN.value())
        .error("Access Denied")
        .message(ex.getMessage())
        .timestamp(Instant.now())
        .build();

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

    ValidationErrorResponse error = ValidationErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Validation Failed")
        .message("Request validation failed")
        .timestamp(Instant.now())
        .errors(errors)
        .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
      IllegalArgumentException ex,
      WebRequest request) {
    log.error("Illegal argument: {}", ex.getMessage());

    ErrorResponse error = ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Bad Request")
        .message(ex.getMessage())
        .timestamp(Instant.now())
        .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }
}
