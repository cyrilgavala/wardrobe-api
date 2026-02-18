package sk.cyrilgavala.wardrobeapi.shared.presentation.dto;

import java.time.Instant;
import java.util.Map;

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