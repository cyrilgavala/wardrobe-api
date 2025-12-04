package sk.cyrilgavala.wardrobeapi.auth.presentation.dto;

import java.time.Instant;
import lombok.Builder;
import sk.cyrilgavala.wardrobeapi.auth.domain.model.UserRole;

@Builder
public record UserResponse(
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

