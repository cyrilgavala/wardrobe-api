package sk.cyrilgavala.wardrobeapi.auth.application.dto;

import java.time.Instant;
import lombok.Builder;
import sk.cyrilgavala.wardrobeapi.auth.domain.model.UserRole;

/**
 * Application layer DTO for User entity. This DTO is used to transfer user data from the
 * application layer to the presentation layer.
 */
@Builder
public record UserDto(
    String id,
    String username,
    String email,
    String firstName,
    String lastName,
    UserRole role,
    Instant createdAt,
    Instant lastLoginAt
) {

}

