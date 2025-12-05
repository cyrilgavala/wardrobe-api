package sk.cyrilgavala.wardrobeapi.auth.presentation.dto;

import java.time.Instant;
import lombok.Builder;

@Builder
public record UserResponse(
    String id,
    String username,
    String email,
    String firstName,
    String lastName,
    String role,
    Instant createdAt,
    Instant lastLoginAt
) {

}

