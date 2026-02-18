package sk.cyrilgavala.wardrobeapi.auth.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Authentication response containing JWT tokens")
public record AuthenticationResponse(
    @Schema(description = "JWT access token for authenticating API requests",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    String accessToken,

    @Schema(description = "JWT refresh token for obtaining new access tokens",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    String refreshToken,

    @Schema(description = "Token type (always 'Bearer')", example = "Bearer")
    String tokenType,

    @Schema(description = "Access token expiration time in seconds", example = "3600")
    Long expiresIn
) {

  public static AuthenticationResponse of(String accessToken, String refreshToken, Long expiresIn) {
    return AuthenticationResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .tokenType("Bearer")
        .expiresIn(expiresIn)
        .build();
  }
}

