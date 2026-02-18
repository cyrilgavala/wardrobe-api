package sk.cyrilgavala.wardrobeapi.shared.presentation.dto;

import java.time.Instant;

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
