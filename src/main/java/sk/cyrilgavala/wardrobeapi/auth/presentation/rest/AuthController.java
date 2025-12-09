package sk.cyrilgavala.wardrobeapi.auth.presentation.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.cyrilgavala.wardrobeapi.auth.application.command.LoginCommand;
import sk.cyrilgavala.wardrobeapi.auth.application.command.RegisterUserCommand;
import sk.cyrilgavala.wardrobeapi.auth.application.dto.UserDto;
import sk.cyrilgavala.wardrobeapi.auth.application.service.AuthenticationService;
import sk.cyrilgavala.wardrobeapi.auth.domain.exception.InvalidCredentialsException;
import sk.cyrilgavala.wardrobeapi.auth.domain.exception.UserNotFoundException;
import sk.cyrilgavala.wardrobeapi.auth.domain.model.User;
import sk.cyrilgavala.wardrobeapi.auth.domain.repository.UserRepository;
import sk.cyrilgavala.wardrobeapi.auth.infrastructure.security.JwtTokenProvider;
import sk.cyrilgavala.wardrobeapi.auth.presentation.dto.AuthenticationResponse;
import sk.cyrilgavala.wardrobeapi.auth.presentation.dto.LoginRequest;
import sk.cyrilgavala.wardrobeapi.auth.presentation.dto.RefreshTokenRequest;
import sk.cyrilgavala.wardrobeapi.auth.presentation.dto.RegisterRequest;
import sk.cyrilgavala.wardrobeapi.auth.presentation.dto.UserResponse;
import sk.cyrilgavala.wardrobeapi.auth.presentation.mapper.UserDtoMapper;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and user registration endpoints")
public class AuthController {

  private final AuthenticationService authenticationService;
  private final UserRepository userRepository;
  private final UserDtoMapper userMapper;
  private final JwtTokenProvider jwtTokenProvider;

  @PostMapping("/register")
  @Operation(
      summary = "Register a new user",
      description = "Creates a new user account with the provided details and returns authentication tokens"
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "User registered successfully with tokens"),
      @ApiResponse(responseCode = "400", description = "Invalid request data"),
      @ApiResponse(responseCode = "409", description = "Username or email already exists")
  })
  public ResponseEntity<AuthenticationResponse> register(
      @Valid @RequestBody RegisterRequest request) {
    log.info("Received registration request for username: {}", request.username());

    // Register user
    RegisterUserCommand command = userMapper.toCommand(request);
    UserDto userDto = authenticationService.register(command);

    // Fetch user entity for JWT generation (infrastructure layer needs it)
    User user = userRepository.findByUsername(userDto.username())
        .orElseThrow(() -> UserNotFoundException.withUsername(userDto.username()));

    // Generate tokens
    String accessToken = jwtTokenProvider.generateAccessToken(user);
    String refreshToken = jwtTokenProvider.generateRefreshToken(user);

    // Build response
    Long expiresIn = jwtTokenProvider.getAccessTokenExpirationMinutes() * 60; // Convert to seconds
    AuthenticationResponse response = AuthenticationResponse.of(
        accessToken,
        refreshToken,
        expiresIn
    );

    log.info("User registered successfully with id: {}, tokens generated", userDto.id());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("/login")
  @Operation(
      summary = "Login user",
      description = "Authenticates a user with username and password and returns authentication tokens"
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User logged in successfully with tokens"),
      @ApiResponse(responseCode = "400", description = "Invalid request data"),
      @ApiResponse(responseCode = "401", description = "Invalid credentials or account locked/disabled")
  })
  public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequest request) {
    log.info("Received login request for username: {}", request.username());

    // Authenticate user
    LoginCommand command = userMapper.toCommand(request);
    UserDto userDto = authenticationService.login(command);

    // Fetch user entity for JWT generation (infrastructure layer needs it)
    User user = userRepository.findByUsername(userDto.username())
        .orElseThrow(() -> UserNotFoundException.withUsername(userDto.username()));

    // Generate tokens
    String accessToken = jwtTokenProvider.generateAccessToken(user);
    String refreshToken = jwtTokenProvider.generateRefreshToken(user);

    // Build response
    Long expiresIn = jwtTokenProvider.getAccessTokenExpirationMinutes() * 60; // Convert to seconds
    AuthenticationResponse response = AuthenticationResponse.of(
        accessToken,
        refreshToken,
        expiresIn
    );

    log.info("User logged in successfully: {}, tokens generated", userDto.username());
    return ResponseEntity.ok(response);
  }

  @PostMapping("/refresh")
  @Operation(
      summary = "Refresh access token",
      description = "Generates a new access token using a valid refresh token"
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid request data"),
      @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
  })
  public ResponseEntity<AuthenticationResponse> refreshToken(
      @Valid @RequestBody RefreshTokenRequest request) {
    log.info("Received token refresh request");

    // Validate refresh token
    if (!jwtTokenProvider.isRefreshToken(request.refreshToken())) {
      log.warn("Token refresh failed: invalid refresh token");
      throw new InvalidCredentialsException("Invalid refresh token");
    }

    // Extract username from refresh token
    String username = jwtTokenProvider.getUsernameFromToken(request.refreshToken());
    if (username == null) {
      log.warn("Token refresh failed: could not extract username from token");
      throw new InvalidCredentialsException("Invalid refresh token");
    }

    // Load user
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> {
          log.warn("Token refresh failed: user not found - {}", username);
          return UserNotFoundException.withUsername(username);
        });

    // Generate new tokens
    String accessToken = jwtTokenProvider.generateAccessToken(user);
    String refreshToken = jwtTokenProvider.generateRefreshToken(user);

    // Build response
    Long expiresIn = jwtTokenProvider.getAccessTokenExpirationMinutes() * 60;
    AuthenticationResponse response = AuthenticationResponse.of(
        accessToken,
        refreshToken,
        expiresIn
    );

    log.info("Token refreshed successfully for user: {}", username);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/me")
  @Operation(
      summary = "Get current user",
      description = "Returns the profile of the currently authenticated user"
  )
  @SecurityRequirement(name = "Bearer Authentication")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "User profile retrieved successfully"),
      @ApiResponse(responseCode = "401", description = "Not authenticated")
  })
  public ResponseEntity<UserResponse> getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      log.warn("Get current user failed: not authenticated");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    String username = authentication.getName();
    log.info("Fetching profile for user: {}", username);

    UserDto userDto = authenticationService.getUserByUsername(username);
    UserResponse response = userMapper.toResponse(userDto);

    return ResponseEntity.ok(response);
  }

  @PostMapping("/logout")
  @Operation(
      summary = "Logout user",
      description = "Logs out the current user (client should discard tokens)"
  )
  @SecurityRequirement(name = "Bearer Authentication")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Logged out successfully"),
      @ApiResponse(responseCode = "401", description = "Not authenticated")
  })
  public ResponseEntity<Void> logout() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication != null && authentication.isAuthenticated()) {
      String username = authentication.getName();
      log.info("User logged out: {}", username);
    }

    // In a stateless JWT setup, logout is handled client-side by discarding the token
    // For token blacklisting, you would add the token to a blacklist here

    return ResponseEntity.ok().build();
  }
}

