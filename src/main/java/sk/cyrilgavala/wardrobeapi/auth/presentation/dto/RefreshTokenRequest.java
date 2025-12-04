package sk.cyrilgavala.wardrobeapi.auth.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to refresh access token using refresh token")
public record RefreshTokenRequest(
    @NotBlank(message = "Refresh token is required")
    @Schema(description = "Refresh token received during login/register", example = "eyJhbGc...")
    String refreshToken
) {

}

