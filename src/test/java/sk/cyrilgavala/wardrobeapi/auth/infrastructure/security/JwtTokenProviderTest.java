package sk.cyrilgavala.wardrobeapi.auth.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;

import io.jsonwebtoken.Claims;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import sk.cyrilgavala.wardrobeapi.auth.domain.model.User;
import sk.cyrilgavala.wardrobeapi.auth.domain.model.UserRole;

class JwtTokenProviderTest {

  private JwtTokenProvider tokenProvider;
  private User testUser;

  @BeforeEach
  void setUp() {
    tokenProvider = new JwtTokenProvider();
    ReflectionTestUtils.setField(tokenProvider, "jwtSecret",
        "test-secret-key-that-is-at-least-512-bits-long-for-HS512-algorithm-to-work-properly");
    ReflectionTestUtils.setField(tokenProvider, "accessTokenExpirationMinutes", 60L);
    ReflectionTestUtils.setField(tokenProvider, "refreshTokenExpirationDays", 7L);

    testUser = User.builder()
        .id("user123")
        .username("johndoe")
        .email("john@example.com")
        .password("encoded_password")
        .firstName("John")
        .lastName("Doe")
        .role(UserRole.USER)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();
  }

  @Test
  void generatesAccessTokenWithUserClaims() {
    String token = tokenProvider.generateAccessToken(testUser);

    assertThat(token).isNotNull();
    assertThat(token).isNotEmpty();
    assertThat(token.split("\\.")).hasSize(3);
  }

  @Test
  void generatesRefreshTokenWithUserClaims() {
    String token = tokenProvider.generateRefreshToken(testUser);

    assertThat(token).isNotNull();
    assertThat(token).isNotEmpty();
    assertThat(token.split("\\.")).hasSize(3);
  }

  @Test
  void accessTokenContainsUsernameAsSubject() {
    String token = tokenProvider.generateAccessToken(testUser);

    Optional<Claims> claims = tokenProvider.validateTokenAndGetClaims(token);

    assertThat(claims).isPresent();
    assertThat(claims.get().getSubject()).isEqualTo("johndoe");
  }

  @Test
  void accessTokenContainsUserIdClaim() {
    String token = tokenProvider.generateAccessToken(testUser);

    Optional<Claims> claims = tokenProvider.validateTokenAndGetClaims(token);

    assertThat(claims).isPresent();
    assertThat(claims.get().get("userId")).isEqualTo("user123");
  }

  @Test
  void accessTokenContainsEmailClaim() {
    String token = tokenProvider.generateAccessToken(testUser);

    Optional<Claims> claims = tokenProvider.validateTokenAndGetClaims(token);

    assertThat(claims).isPresent();
    assertThat(claims.get().get("email")).isEqualTo("john@example.com");
  }

  @Test
  void accessTokenContainsRoleClaim() {
    String token = tokenProvider.generateAccessToken(testUser);

    Optional<Claims> claims = tokenProvider.validateTokenAndGetClaims(token);

    assertThat(claims).isPresent();
    assertThat(claims.get().get("role")).isEqualTo("USER");
  }

  @Test
  void accessTokenContainsTokenTypeClaim() {
    String token = tokenProvider.generateAccessToken(testUser);

    Optional<Claims> claims = tokenProvider.validateTokenAndGetClaims(token);

    assertThat(claims).isPresent();
    assertThat(claims.get().get("tokenType")).isEqualTo("access");
  }

  @Test
  void refreshTokenContainsTokenTypeClaim() {
    String token = tokenProvider.generateRefreshToken(testUser);

    Optional<Claims> claims = tokenProvider.validateTokenAndGetClaims(token);

    assertThat(claims).isPresent();
    assertThat(claims.get().get("tokenType")).isEqualTo("refresh");
  }

  @Test
  void accessTokenContainsIssuedAtClaim() {
    Instant before = Instant.now();
    String token = tokenProvider.generateAccessToken(testUser);
    Instant after = Instant.now();

    Optional<Claims> claims = tokenProvider.validateTokenAndGetClaims(token);

    assertThat(claims).isPresent();
    assertThat(claims.get().getIssuedAt()).isNotNull();
    assertThat(claims.get().getIssuedAt().toInstant()).isBetween(
        before.minus(1, ChronoUnit.SECONDS), after.plus(1, ChronoUnit.SECONDS));
  }

  @Test
  void accessTokenContainsExpirationClaim() {
    String token = tokenProvider.generateAccessToken(testUser);

    Optional<Claims> claims = tokenProvider.validateTokenAndGetClaims(token);

    assertThat(claims).isPresent();
    assertThat(claims.get().getExpiration()).isNotNull();
  }

  @Test
  void accessTokenExpiresAfterConfiguredMinutes() {
    Instant before = Instant.now().plus(60, ChronoUnit.MINUTES);
    String token = tokenProvider.generateAccessToken(testUser);
    Instant after = Instant.now().plus(60, ChronoUnit.MINUTES);

    Optional<Claims> claims = tokenProvider.validateTokenAndGetClaims(token);

    assertThat(claims).isPresent();
    assertThat(claims.get().getExpiration().toInstant()).isBetween(
        before.minus(1, ChronoUnit.SECONDS), after.plus(1, ChronoUnit.SECONDS));
  }

  @Test
  void refreshTokenExpiresAfterConfiguredDays() {
    Instant before = Instant.now().plus(7, ChronoUnit.DAYS);
    String token = tokenProvider.generateRefreshToken(testUser);
    Instant after = Instant.now().plus(7, ChronoUnit.DAYS);

    Optional<Claims> claims = tokenProvider.validateTokenAndGetClaims(token);

    assertThat(claims).isPresent();
    assertThat(claims.get().getExpiration().toInstant()).isBetween(
        before.minus(1, ChronoUnit.SECONDS), after.plus(1, ChronoUnit.SECONDS));
  }

  @Test
  void accessTokenContainsUniqueId() {
    String token1 = tokenProvider.generateAccessToken(testUser);
    String token2 = tokenProvider.generateAccessToken(testUser);

    Optional<Claims> claims1 = tokenProvider.validateTokenAndGetClaims(token1);
    Optional<Claims> claims2 = tokenProvider.validateTokenAndGetClaims(token2);

    assertThat(claims1).isPresent();
    assertThat(claims2).isPresent();
    assertThat(claims1.get().getId()).isNotEqualTo(claims2.get().getId());
  }

  @Test
  void accessTokenContainsIssuerClaim() {
    String token = tokenProvider.generateAccessToken(testUser);

    Optional<Claims> claims = tokenProvider.validateTokenAndGetClaims(token);

    assertThat(claims).isPresent();
    assertThat(claims.get().getIssuer()).isEqualTo("wardrobe-api");
  }

  @Test
  void accessTokenContainsAudienceClaim() {
    String token = tokenProvider.generateAccessToken(testUser);

    Optional<Claims> claims = tokenProvider.validateTokenAndGetClaims(token);

    assertThat(claims).isPresent();
    assertThat(claims.get().getAudience()).contains("wardrobe-ui");
  }

  @Test
  void validatesValidAccessToken() {
    String token = tokenProvider.generateAccessToken(testUser);

    Optional<Claims> claims = tokenProvider.validateTokenAndGetClaims(token);

    assertThat(claims).isPresent();
  }

  @Test
  void validatesValidRefreshToken() {
    String token = tokenProvider.generateRefreshToken(testUser);

    Optional<Claims> claims = tokenProvider.validateTokenAndGetClaims(token);

    assertThat(claims).isPresent();
  }

  @Test
  void returnsEmptyForInvalidToken() {
    String invalidToken = "invalid.jwt.token";

    Optional<Claims> claims = tokenProvider.validateTokenAndGetClaims(invalidToken);

    assertThat(claims).isEmpty();
  }

  @Test
  void returnsEmptyForMalformedToken() {
    String malformedToken = "malformed";

    Optional<Claims> claims = tokenProvider.validateTokenAndGetClaims(malformedToken);

    assertThat(claims).isEmpty();
  }

  @Test
  void returnsEmptyForNullToken() {
    Optional<Claims> claims = tokenProvider.validateTokenAndGetClaims(null);

    assertThat(claims).isEmpty();
  }

  @Test
  void returnsEmptyForEmptyToken() {
    Optional<Claims> claims = tokenProvider.validateTokenAndGetClaims("");

    assertThat(claims).isEmpty();
  }

  @Test
  void returnsEmptyForTokenWithInvalidSignature() {
    String token = tokenProvider.generateAccessToken(testUser);
    String tamperedToken = token.substring(0, token.length() - 10) + "tampered12";

    Optional<Claims> claims = tokenProvider.validateTokenAndGetClaims(tamperedToken);

    assertThat(claims).isEmpty();
  }

  @Test
  void extractsUsernameFromValidToken() {
    String token = tokenProvider.generateAccessToken(testUser);

    String username = tokenProvider.getUsernameFromToken(token);

    assertThat(username).isEqualTo("johndoe");
  }

  @Test
  void returnsNullForUsernameFromInvalidToken() {
    String invalidToken = "invalid.jwt.token";

    String username = tokenProvider.getUsernameFromToken(invalidToken);

    assertThat(username).isNull();
  }

  @Test
  void identifiesAccessTokenCorrectly() {
    String token = tokenProvider.generateAccessToken(testUser);

    boolean isAccessToken = tokenProvider.isAccessToken(token);

    assertThat(isAccessToken).isTrue();
  }

  @Test
  void identifiesRefreshTokenAsNotAccessToken() {
    String token = tokenProvider.generateRefreshToken(testUser);

    boolean isAccessToken = tokenProvider.isAccessToken(token);

    assertThat(isAccessToken).isFalse();
  }

  @Test
  void returnsFalseForInvalidTokenAsAccessToken() {
    String invalidToken = "invalid.jwt.token";

    boolean isAccessToken = tokenProvider.isAccessToken(invalidToken);

    assertThat(isAccessToken).isFalse();
  }

  @Test
  void identifiesRefreshTokenCorrectly() {
    String token = tokenProvider.generateRefreshToken(testUser);

    boolean isRefreshToken = tokenProvider.isRefreshToken(token);

    assertThat(isRefreshToken).isTrue();
  }

  @Test
  void identifiesAccessTokenAsNotRefreshToken() {
    String token = tokenProvider.generateAccessToken(testUser);

    boolean isRefreshToken = tokenProvider.isRefreshToken(token);

    assertThat(isRefreshToken).isFalse();
  }

  @Test
  void returnsFalseForInvalidTokenAsRefreshToken() {
    String invalidToken = "invalid.jwt.token";

    boolean isRefreshToken = tokenProvider.isRefreshToken(invalidToken);

    assertThat(isRefreshToken).isFalse();
  }

  @Test
  void generatesTokenForAdminUser() {
    User adminUser = User.builder()
        .id("admin1")
        .username("admin")
        .email("admin@example.com")
        .password("encoded_password")
        .firstName("Admin")
        .lastName("User")
        .role(UserRole.ADMIN)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();

    String token = tokenProvider.generateAccessToken(adminUser);

    Optional<Claims> claims = tokenProvider.validateTokenAndGetClaims(token);

    assertThat(claims).isPresent();
    assertThat(claims.get().get("role")).isEqualTo("ADMIN");
  }

  @Test
  void generatesTokenWithSpecialCharactersInUsername() {
    User specialUser = User.builder()
        .id("user456")
        .username("user.name-123")
        .email("user+tag@example.com")
        .password("encoded_password")
        .firstName("User")
        .lastName("Name")
        .role(UserRole.USER)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();

    String token = tokenProvider.generateAccessToken(specialUser);

    Optional<Claims> claims = tokenProvider.validateTokenAndGetClaims(token);

    assertThat(claims).isPresent();
    assertThat(claims.get().getSubject()).isEqualTo("user.name-123");
    assertThat(claims.get().get("email")).isEqualTo("user+tag@example.com");
  }

  @Test
  void generatesDifferentTokensForSameUserOnMultipleCalls() {
    String token1 = tokenProvider.generateAccessToken(testUser);
    String token2 = tokenProvider.generateAccessToken(testUser);

    assertThat(token1).isNotEqualTo(token2);
  }

  @Test
  void accessTokenAndRefreshTokenAreDifferent() {
    String accessToken = tokenProvider.generateAccessToken(testUser);
    String refreshToken = tokenProvider.generateRefreshToken(testUser);

    assertThat(accessToken).isNotEqualTo(refreshToken);
  }
}

