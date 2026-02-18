package sk.cyrilgavala.wardrobeapi.auth.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.Builder;

@Builder
@Schema(description = "User profile information")
public record UserResponse(
    @Schema(description = "Unique user identifier", example = "507f1f77bcf86cd799439011")
    String id,

    @Schema(description = "Username", example = "johndoe")
    String username,

    @Schema(description = "Email address", example = "john.doe@example.com")
    String email,

    @Schema(description = "First name", example = "John")
    String firstName,

    @Schema(description = "Last name", example = "Doe")
    String lastName,

    @Schema(description = "User role", example = "USER")
    String role,

    @Schema(description = "Account creation timestamp", example = "2023-12-01T10:00:00Z")
    Instant createdAt,

    @Schema(description = "Last login timestamp", example = "2023-12-15T14:30:00Z")
    Instant lastLoginAt
) {

}

