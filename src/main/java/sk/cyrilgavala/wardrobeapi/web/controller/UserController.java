package sk.cyrilgavala.wardrobeapi.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import sk.cyrilgavala.wardrobeapi.security.TokenProvider;
import sk.cyrilgavala.wardrobeapi.service.UserService;
import sk.cyrilgavala.wardrobeapi.web.dto.AuthResponse;
import sk.cyrilgavala.wardrobeapi.web.dto.ErrorResponse;
import sk.cyrilgavala.wardrobeapi.web.dto.LoginRequest;
import sk.cyrilgavala.wardrobeapi.web.dto.RegisterRequest;
import sk.cyrilgavala.wardrobeapi.web.dto.UserResponse;

@Tag(name = "User Authentication", description = "User registration and authentication endpoints")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final AuthenticationManager authenticationManager;
  private final TokenProvider tokenProvider;

  @Operation(summary = "User login", description = "Authenticate user and return JWT token")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully authenticated",
          content = @Content(schema = @Schema(implementation = AuthResponse.class))),
      @ApiResponse(responseCode = "401", description = "Invalid credentials",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "400", description = "Invalid request",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public AuthResponse login(@Valid @RequestBody LoginRequest loginRequest) {
    UserResponse user = userService.getByUsername(loginRequest.getUsername());
    String token = authenticateAndGetToken(loginRequest.getUsername(),
        new String(Base64.getDecoder().decode(loginRequest.getPassword())), user.getEmail());
    return new AuthResponse(token);
  }

  @Operation(summary = "User registration", description = "Register a new user and return JWT token")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Successfully registered",
          content = @Content(schema = @Schema(implementation = AuthResponse.class))),
      @ApiResponse(responseCode = "409", description = "User already exists",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "400", description = "Invalid request",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public AuthResponse register(@Valid @RequestBody RegisterRequest registerRequest)
      throws DataIntegrityViolationException {
    userService.saveUser(registerRequest);
    String token = authenticateAndGetToken(registerRequest.getUsername(),
        new String(Base64.getDecoder().decode(registerRequest.getPassword())),
        registerRequest.getEmail());
    return new AuthResponse(token);
  }

  private String authenticateAndGetToken(String username, String password, String email) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(username, password));
    return tokenProvider.generate(authentication, email);
  }

}
