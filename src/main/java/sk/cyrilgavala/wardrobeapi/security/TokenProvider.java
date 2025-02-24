package sk.cyrilgavala.wardrobeapi.security;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TokenProvider {

	private static final String TOKEN_TYPE = "JWT";
	private static final String TOKEN_ISSUER = "wardrobe-api";
	private static final String TOKEN_AUDIENCE = "wardrobe-ui";
	@Value("${app.jwt.secret}")
	private String jwtSecret;
	@Value("${app.jwt.expiration.minutes}")
	private Long jwtExpirationMinutes;

	public String generate(Authentication authentication, String email) {
		UserDetails user = (User) authentication.getPrincipal();

		String roles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

		byte[] signingKey = jwtSecret.getBytes();

		return Jwts.builder().header().type(TOKEN_TYPE).and().signWith(Keys.hmacShaKeyFor(signingKey), Jwts.SIG.HS512).expiration(Date.from(ZonedDateTime.now().plusMinutes(jwtExpirationMinutes).toInstant())).issuedAt(Date.from(ZonedDateTime.now().toInstant())).id(UUID.randomUUID().toString()).audience().add(TOKEN_AUDIENCE).and().issuer(TOKEN_ISSUER).subject(user.getUsername()).claim("roles", roles).claim("email", email).compact();
	}

	public Optional<Jws<Claims>> validateTokenAndGetJws(String token) {
		try {
			SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

			Jws<Claims> jws = Jwts.parser().verifyWith(key).build().parseSignedClaims(token);

			return Optional.of(jws);
		} catch (ExpiredJwtException exception) {
			log.error("Request to parse expired JWT : {} failed : {}", token, exception.getMessage());
		} catch (UnsupportedJwtException exception) {
			log.error("Request to parse unsupported JWT : {} failed : {}", token, exception.getMessage());
		} catch (MalformedJwtException exception) {
			log.error("Request to parse invalid JWT : {} failed : {}", token, exception.getMessage());
		} catch (SignatureException exception) {
			log.error("Request to parse JWT with invalid signature : {} failed : {}", token, exception.getMessage());
		} catch (IllegalArgumentException exception) {
			log.error("Request to parse empty or null JWT : {} failed : {}", token, exception.getMessage());
		}
		return Optional.empty();
	}
}
