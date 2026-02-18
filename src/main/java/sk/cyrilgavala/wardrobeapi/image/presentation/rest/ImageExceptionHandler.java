package sk.cyrilgavala.wardrobeapi.image.presentation.rest;

import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import sk.cyrilgavala.wardrobeapi.image.domain.exception.ImageNotFoundException;
import sk.cyrilgavala.wardrobeapi.image.domain.exception.InvalidImageException;
import sk.cyrilgavala.wardrobeapi.shared.presentation.dto.ErrorResponse;

@Slf4j
@RestControllerAdvice
public class ImageExceptionHandler {

  @ExceptionHandler(ImageNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleImageNotFoundException(
      ImageNotFoundException ex,
      WebRequest request) {
    log.error("Image not found: {}", ex.getMessage());

    ErrorResponse error = ErrorResponse.builder()
        .timestamp(Instant.now())
        .status(HttpStatus.NOT_FOUND.value())
        .error("Not Found")
        .message(ex.getMessage())
        .build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(InvalidImageException.class)
  public ResponseEntity<ErrorResponse> handleInvalidImageException(
      InvalidImageException ex,
      WebRequest request) {
    log.error("Invalid image: {}", ex.getMessage());

    ErrorResponse error = ErrorResponse.builder()
        .timestamp(Instant.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Bad Request")
        .message(ex.getMessage())
        .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(
      MaxUploadSizeExceededException ex,
      WebRequest request) {
    log.error("File size exceeded: {}", ex.getMessage());

    ErrorResponse error = ErrorResponse.builder()
        .timestamp(Instant.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Bad Request")
        .message("File size exceeds maximum allowed size of 20MB")
        .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }
}