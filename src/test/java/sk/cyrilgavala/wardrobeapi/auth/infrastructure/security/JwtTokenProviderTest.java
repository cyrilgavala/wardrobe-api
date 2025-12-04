package sk.cyrilgavala.wardrobeapi.auth.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;

import io.jsonwebtoken.Claims;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import sk.cyrilgavala.wardrobeapi.auth.domain.model.User;

class JwtTokenProviderTest {

  private JwtTokenProvider jwtTokenProvider;
  private User testUser;

  @BeforeEach
  void setUp() {
    jwtTokenProvider = new JwtTokenProvider();

    // Set private fields using reflection - HS512 requires at least 512 bits (64 bytes)
    String secretKey = "test-secret-key-that-is-at-least-64-bytes-long-for-hs512-algorithm-requirements-very-secure";
    ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", secretKey);
    ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenExpirationMinutes", 60L);
    ReflectionTestUtils.setField(jwtTokenProvider, "refreshTokenExpirationDays", 7L);

    testUser = User.create(
        "johndoe",
        "john@example.com",
        "encodedPassword",
        "John",
        "Doe"
    );
  }

  @Test
  void generateAccessToken_shouldGenerateValidToken() {
    // When
    String token = jwtTokenProvider.generateAccessToken(testUser);

    // Then
    assertThat(token).isNotNull();
    assertThat(token).isNotEmpty();
    assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts: header.payload.signature
  }

  @Test
  void generateRefreshToken_shouldGenerateValidToken() {
    // When
    String token = jwtTokenProvider.generateRefreshToken(testUser);

    // Then
    assertThat(token).isNotNull();
    assertThat(token).isNotEmpty();
    assertThat(token.split("\\.")).hasSize(3);
  }

  @Test
  void isAccessToken_shouldReturnTrue_forAccessToken() {
    // Given
    String token = jwtTokenProvider.generateAccessToken(testUser);

    // When
    boolean result = jwtTokenProvider.isAccessToken(token);

    // Then
    assertThat(result).isTrue();
  }

  @Test
  void isAccessToken_shouldReturnFalse_forRefreshToken() {
    // Given
    String token = jwtTokenProvider.generateRefreshToken(testUser);

    // When
    boolean result = jwtTokenProvider.isAccessToken(token);

    // Then
    assertThat(result).isFalse();
  }

  @Test
  void isRefreshToken_shouldReturnTrue_forRefreshToken() {
    // Given
    String token = jwtTokenProvider.generateRefreshToken(testUser);

    // When
    boolean result = jwtTokenProvider.isRefreshToken(token);

    // Then
    assertThat(result).isTrue();
  }

  @Test
  void isRefreshToken_shouldReturnFalse_forAccessToken() {
    // Given
    String token = jwtTokenProvider.generateAccessToken(testUser);

    // When
    boolean result = jwtTokenProvider.isRefreshToken(token);

    // Then
    assertThat(result).isFalse();
  }

  @Test
  void getUsernameFromToken_shouldExtractUsername() {
    // Given
    String token = jwtTokenProvider.generateAccessToken(testUser);

    // When
    String username = jwtTokenProvider.getUsernameFromToken(token);

    // Then
    assertThat(username).isEqualTo("johndoe");
  }

  @Test
  void validateTokenAndGetClaims_shouldReturnClaims_forValidToken() {
    // Given
    String token = jwtTokenProvider.generateAccessToken(testUser);

    // When
    Optional<Claims> claims = jwtTokenProvider.validateTokenAndGetClaims(token);

    // Then
    assertThat(claims).isPresent();
    assertThat(claims.get().getSubject()).isEqualTo("johndoe");
    assertThat(claims.get().get("email")).isEqualTo("john@example.com");
    assertThat(claims.get().get("role")).isEqualTo("USER");
  }

  @Test
  void validateTokenAndGetClaims_shouldReturnEmpty_forInvalidToken() {
    // Given
    String invalidToken = "invalid.token.here";

    // When
    Optional<Claims> claims = jwtTokenProvider.validateTokenAndGetClaims(invalidToken);

    // Then
    assertThat(claims).isEmpty();
  }

  @Test
  void generateAccessToken_shouldIncludeAllUserClaims() {
    // Given
    User adminUser = testUser.promoteToAdmin();
    String token = jwtTokenProvider.generateAccessToken(adminUser);

    // When
    Optional<Claims> claims = jwtTokenProvider.validateTokenAndGetClaims(token);

    // Then
    assertThat(claims).isPresent();
    assertThat(claims.get().getSubject()).isEqualTo("johndoe");
    assertThat(claims.get().get("email")).isEqualTo("john@example.com");
    assertThat(claims.get().get("role")).isEqualTo("ADMIN");
    assertThat(claims.get().get("tokenType")).isEqualTo("access");
    assertThat(claims.get().getId()).isNotNull(); // JWT ID
    assertThat(claims.get().getIssuer()).isEqualTo("wardrobe-api");
    assertThat(claims.get().getAudience()).contains("wardrobe-ui");
  }

  @Test
  void getUsernameFromToken_shouldReturnNull_forInvalidToken() {
    // Given
    String invalidToken = "invalid.token.here";

    // When
    String username = jwtTokenProvider.getUsernameFromToken(invalidToken);

    // Then
    assertThat(username).isNull();
  }

  @Test
  void accessTokenAndRefreshToken_shouldBeDifferent() {
    // When
    String accessToken = jwtTokenProvider.generateAccessToken(testUser);
    String refreshToken = jwtTokenProvider.generateRefreshToken(testUser);

    // Then
    assertThat(accessToken).isNotEqualTo(refreshToken);
  }
}

