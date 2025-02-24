package sk.cyrilgavala.wardrobeapi.web.controller;

import java.util.Base64;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sk.cyrilgavala.wardrobeapi.security.TokenProvider;
import sk.cyrilgavala.wardrobeapi.service.UserService;
import sk.cyrilgavala.wardrobeapi.web.dto.AuthResponse;
import sk.cyrilgavala.wardrobeapi.web.dto.LoginRequest;
import sk.cyrilgavala.wardrobeapi.web.dto.RegisterRequest;
import sk.cyrilgavala.wardrobeapi.web.dto.UserResponse;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {

	private final UserService userService;
	private final AuthenticationManager authenticationManager;
	private final TokenProvider tokenProvider;

	@PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public AuthResponse login(@Valid @RequestBody LoginRequest loginRequest) {
		UserResponse user = userService.getByUsername(loginRequest.getUsername());
		String token = authenticateAndGetToken(loginRequest.getUsername(), new String(Base64.getDecoder().decode(loginRequest.getPassword())), user.getEmail());
		return new AuthResponse(token);
	}

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public AuthResponse register(@Valid @RequestBody RegisterRequest registerRequest) throws DataIntegrityViolationException {
		userService.saveUser(registerRequest);
		String token = authenticateAndGetToken(registerRequest.getUsername(), new String(Base64.getDecoder().decode(registerRequest.getPassword())), registerRequest.getEmail());
		return new AuthResponse(token);
	}

	private String authenticateAndGetToken(String username, String password, String email) {
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		return tokenProvider.generate(authentication, email);
	}

}
