package sk.cyrilgavala.wardrobeapi.auth.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sk.cyrilgavala.wardrobeapi.auth.domain.model.User;

@Slf4j
@Component
public class JwtTokenProvider {

  private static final String TOKEN_TYPE = "JWT";
  private static final String TOKEN_ISSUER = "wardrobe-api";
  private static final String TOKEN_AUDIENCE = "wardrobe-ui";

  @Value("${app.jwt.secret}")
  private String jwtSecret;

  @Value("${app.jwt.expiration.minutes:60}")
  private Long accessTokenExpirationMinutes;

  @Value("${app.jwt.refresh-expiration.days:7}")
  private Long refreshTokenExpirationDays;

  public String generateAccessToken(User user) {
    return generateToken(user, accessTokenExpirationMinutes, ChronoUnit.MINUTES, "access");
  }

  public String generateRefreshToken(User user) {
    return generateToken(user, refreshTokenExpirationDays, ChronoUnit.DAYS, "refresh");
  }

  private String generateToken(User user, Long expiration, ChronoUnit unit, String tokenType) {
    byte[] signingKey = jwtSecret.getBytes();
    Instant now = Instant.now();
    Instant expirationTime = now.plus(expiration, unit);

    return Jwts.builder()
        .header().type(TOKEN_TYPE).and()
        .signWith(Keys.hmacShaKeyFor(signingKey), Jwts.SIG.HS512)
        .expiration(Date.from(expirationTime))
        .issuedAt(Date.from(now))
        .id(UUID.randomUUID().toString())
        .issuer(TOKEN_ISSUER)
        .audience().add(TOKEN_AUDIENCE).and()
        .subject(user.username())
        .claim("userId", user.id())
        .claim("email", user.email())
        .claim("role", user.role().name())
        .claim("tokenType", tokenType)
        .compact();
  }

  public Optional<Claims> validateTokenAndGetClaims(String token) {
    try {
      SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
      Claims claims = Jwts.parser()
          .verifyWith(key)
          .build()
          .parseSignedClaims(token)
          .getPayload();
      return Optional.of(claims);
    } catch (ExpiredJwtException exception) {
      log.error("Request to parse expired JWT: {} failed: {}", token, exception.getMessage());
    } catch (UnsupportedJwtException exception) {
      log.error("Request to parse unsupported JWT: {} failed: {}", token, exception.getMessage());
    } catch (MalformedJwtException exception) {
      log.error("Request to parse invalid JWT: {} failed: {}", token, exception.getMessage());
    } catch (SignatureException exception) {
      log.error("Request to parse JWT with invalid signature: {} failed: {}", token,
          exception.getMessage());
    } catch (IllegalArgumentException exception) {
      log.error("Request to parse empty or null JWT: {} failed: {}", token, exception.getMessage());
    }
    return Optional.empty();
  }

  public String getUsernameFromToken(String token) {
    return validateTokenAndGetClaims(token)
        .map(Claims::getSubject)
        .orElse(null);
  }

  public boolean isAccessToken(String token) {
    return validateTokenAndGetClaims(token)
        .map(claims -> "access".equals(claims.get("tokenType")))
        .orElse(false);
  }

  public boolean isRefreshToken(String token) {
    return validateTokenAndGetClaims(token)
        .map(claims -> "refresh".equals(claims.get("tokenType")))
        .orElse(false);
  }
}

