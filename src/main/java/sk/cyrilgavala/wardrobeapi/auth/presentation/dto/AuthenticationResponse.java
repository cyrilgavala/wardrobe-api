package sk.cyrilgavala.wardrobeapi.auth.presentation.dto;

import lombok.Builder;

@Builder
public record AuthenticationResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
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

