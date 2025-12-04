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
  void validateTokenAndGetClaims_shouldReturnEmpty_forMalformedToken() {
    // Given
    String malformedToken = "not.a.valid.jwt.structure.at.all";

    // When
    Optional<Claims> claims = jwtTokenProvider.validateTokenAndGetClaims(malformedToken);

    // Then
    assertThat(claims).isEmpty();
  }

  @Test
  void validateTokenAndGetClaims_shouldReturnEmpty_forNullToken() {
    // Given
    String nullToken = null;

    // When
    Optional<Claims> claims = jwtTokenProvider.validateTokenAndGetClaims(nullToken);

    // Then
    assertThat(claims).isEmpty();
  }

  @Test
  void validateTokenAndGetClaims_shouldReturnEmpty_forEmptyToken() {
    // Given
    String emptyToken = "";

    // When
    Optional<Claims> claims = jwtTokenProvider.validateTokenAndGetClaims(emptyToken);

    // Then
    assertThat(claims).isEmpty();
  }

  @Test
  void validateTokenAndGetClaims_shouldReturnEmpty_forTokenWithInvalidSignature() {
    // Given
    String token = jwtTokenProvider.generateAccessToken(testUser);
    // Tamper with the token by changing the signature (last part)
    String[] parts = token.split("\\.");
    String tamperedToken = parts[0] + "." + parts[1] + ".InvalidSignature";

    // When
    Optional<Claims> claims = jwtTokenProvider.validateTokenAndGetClaims(tamperedToken);

    // Then
    assertThat(claims).isEmpty();
  }

  @Test
  void validateTokenAndGetClaims_shouldReturnEmpty_forExpiredToken() {
    // Given
    // Set very short expiration to create an expired token
    ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenExpirationMinutes", -1L);
    String expiredToken = jwtTokenProvider.generateAccessToken(testUser);
    // Restore normal expiration
    ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenExpirationMinutes", 60L);

    // When
    Optional<Claims> claims = jwtTokenProvider.validateTokenAndGetClaims(expiredToken);

    // Then
    assertThat(claims).isEmpty();
  }

  @Test
  void validateTokenAndGetClaims_shouldReturnEmpty_forTokenSignedWithDifferentSecret() {
    // Given
    // Create a token with a different secret
    String originalSecret = "test-secret-key-that-is-at-least-64-bytes-long-for-hs512-algorithm-requirements-very-secure";
    String differentSecret = "different-secret-key-that-is-also-at-least-64-bytes-long-for-hs512-algorithm-test-security";

    ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", differentSecret);
    String tokenWithDifferentSecret = jwtTokenProvider.generateAccessToken(testUser);

    // Restore original secret
    ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", originalSecret);

    // When
    Optional<Claims> claims = jwtTokenProvider.validateTokenAndGetClaims(tokenWithDifferentSecret);

    // Then
    assertThat(claims).isEmpty();
  }

  @Test
  void validateTokenAndGetClaims_shouldReturnEmpty_forWhitespaceToken() {
    // Given
    String whitespaceToken = "   ";

    // When
    Optional<Claims> claims = jwtTokenProvider.validateTokenAndGetClaims(whitespaceToken);

    // Then
    assertThat(claims).isEmpty();
  }

  @Test
  void validateTokenAndGetClaims_shouldReturnEmpty_forTokenWithOnlyTwoParts() {
    // Given
    String incompleteTwoPartToken = "header.payload";

    // When
    Optional<Claims> claims = jwtTokenProvider.validateTokenAndGetClaims(incompleteTwoPartToken);

    // Then
    assertThat(claims).isEmpty();
  }

  @Test
  void validateTokenAndGetClaims_shouldReturnEmpty_forTokenWithExtraParts() {
    // Given
    String tokenWithExtraParts = "header.payload.signature.extra";

    // When
    Optional<Claims> claims = jwtTokenProvider.validateTokenAndGetClaims(tokenWithExtraParts);

    // Then
    assertThat(claims).isEmpty();
  }

  @Test
  void validateTokenAndGetClaims_shouldReturnClaims_forValidRefreshToken() {
    // Given
    String refreshToken = jwtTokenProvider.generateRefreshToken(testUser);

    // When
    Optional<Claims> claims = jwtTokenProvider.validateTokenAndGetClaims(refreshToken);

    // Then
    assertThat(claims).isPresent();
    assertThat(claims.get().getSubject()).isEqualTo("johndoe");
    assertThat(claims.get().get("tokenType")).isEqualTo("refresh");
  }

  @Test
  void validateTokenAndGetClaims_shouldReturnEmpty_forTokenWithTamperedPayload() {
    // Given
    String token = jwtTokenProvider.generateAccessToken(testUser);
    String[] parts = token.split("\\.");
    // Tamper with the payload (middle part) by replacing with a different base64 string
    String tamperedToken = parts[0] + ".ZXlKemRXSWlPaUpoWkdGdGRHVnpkQ0o5" + "." + parts[2];

    // When
    Optional<Claims> claims = jwtTokenProvider.validateTokenAndGetClaims(tamperedToken);

    // Then
    assertThat(claims).isEmpty();
  }

  @Test
  void validateTokenAndGetClaims_shouldReturnEmpty_forTokenWithTamperedHeader() {
    // Given
    String token = jwtTokenProvider.generateAccessToken(testUser);
    String[] parts = token.split("\\.");
    // Tamper with the header (first part)
    String tamperedToken =
        "ZXlKaGJHY2lPaUpJVXpJMU5pSXNJblI1Y0NJNklrcFhWQ0o5" + "." + parts[1] + "." + parts[2];

    // When
    Optional<Claims> claims = jwtTokenProvider.validateTokenAndGetClaims(tamperedToken);

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

