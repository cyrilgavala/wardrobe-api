package sk.cyrilgavala.wardrobeapi.auth.presentation.rest;

import static java.util.Collections.emptyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import sk.cyrilgavala.wardrobeapi.auth.application.service.AuthenticationService;
import sk.cyrilgavala.wardrobeapi.auth.domain.exception.DuplicateUserException;
import sk.cyrilgavala.wardrobeapi.auth.domain.exception.InvalidCredentialsException;
import sk.cyrilgavala.wardrobeapi.auth.domain.model.User;
import sk.cyrilgavala.wardrobeapi.auth.domain.repository.UserRepository;
import sk.cyrilgavala.wardrobeapi.auth.infrastructure.security.JwtTokenProvider;
import sk.cyrilgavala.wardrobeapi.auth.presentation.dto.LoginRequest;
import sk.cyrilgavala.wardrobeapi.auth.presentation.dto.RegisterRequest;
import sk.cyrilgavala.wardrobeapi.auth.presentation.mapper.UserMapper;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

  private MockMvc mockMvc;

  @Mock
  private AuthenticationService authenticationService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private JwtTokenProvider jwtTokenProvider;

  @InjectMocks
  private UserMapper userMapper = new UserMapper();

  @InjectMocks
  private AuthController authController;

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    authController = new AuthController(
        authenticationService,
        userRepository,
        userMapper,
        jwtTokenProvider
    );
    mockMvc = MockMvcBuilders.standaloneSetup(authController)
        .setControllerAdvice(new AuthExceptionHandler())
        .build();
  }

  private User createTestUser() {
    return User.create(
        "johndoe",
        "john@example.com",
        "encodedPassword",
        "John",
        "Doe"
    );
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  // ===== Registration Tests =====

  @Test
  void register_shouldReturnCreated_withValidRequest() throws Exception {
    // Given
    RegisterRequest request = RegisterRequest.builder()
        .username("johndoe")
        .email("john@example.com")
        .password("SecurePass123!")
        .firstName("John")
        .lastName("Doe")
        .build();

    User user = createTestUser();
    when(authenticationService.register(any())).thenReturn(user);
    when(jwtTokenProvider.generateAccessToken(any())).thenReturn("access-token");
    when(jwtTokenProvider.generateRefreshToken(any())).thenReturn("refresh-token");

    // When & Then
    mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.accessToken").value("access-token"))
        .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
        .andExpect(jsonPath("$.tokenType").value("Bearer"))
        .andExpect(jsonPath("$.expiresIn").exists());
  }

  @Test
  void register_shouldReturnConflict_whenUsernameExists() throws Exception {
    // Given
    RegisterRequest request = RegisterRequest.builder()
        .username("johndoe")
        .email("john@example.com")
        .password("SecurePass123!")
        .firstName("John")
        .lastName("Doe")
        .build();

    when(authenticationService.register(any()))
        .thenThrow(DuplicateUserException.withUsername("johndoe"));

    // When & Then
    mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict());
  }

  // ===== Login Tests =====

  @Test
  void login_shouldReturnOk_withValidCredentials() throws Exception {
    // Given
    LoginRequest request = new LoginRequest("johndoe", "SecurePass123!");
    User user = createTestUser();

    when(authenticationService.login(any())).thenReturn(user);
    when(jwtTokenProvider.generateAccessToken(any())).thenReturn("access-token");
    when(jwtTokenProvider.generateRefreshToken(any())).thenReturn("refresh-token");

    // When & Then
    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value("access-token"))
        .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
        .andExpect(jsonPath("$.tokenType").value("Bearer"));
  }

  @Test
  void login_shouldReturnUnauthorized_withInvalidCredentials() throws Exception {
    // Given
    LoginRequest request = new LoginRequest("johndoe", "wrongPassword");

    when(authenticationService.login(any()))
        .thenThrow(new InvalidCredentialsException("Invalid username or password"));

    // When & Then
    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }

  // ===== Refresh Token Tests =====

  @Test
  void refreshToken_shouldReturnNewTokens_withValidRefreshToken() throws Exception {
    // Given
    String refreshToken = "valid-refresh-token";
    User user = createTestUser();

    when(jwtTokenProvider.isRefreshToken(refreshToken)).thenReturn(true);
    when(jwtTokenProvider.getUsernameFromToken(refreshToken)).thenReturn("johndoe");
    when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(user));
    when(jwtTokenProvider.generateAccessToken(any())).thenReturn("new-access-token");
    when(jwtTokenProvider.generateRefreshToken(any())).thenReturn("new-refresh-token");

    // When & Then
    mockMvc.perform(post("/api/auth/refresh")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value("new-access-token"))
        .andExpect(jsonPath("$.refreshToken").value("new-refresh-token"))
        .andExpect(jsonPath("$.tokenType").value("Bearer"))
        .andExpect(jsonPath("$.expiresIn").exists());
  }

  @Test
  void refreshToken_shouldReturnUnauthorized_withInvalidRefreshToken() throws Exception {
    // Given
    String invalidToken = "invalid-refresh-token";

    when(jwtTokenProvider.isRefreshToken(invalidToken)).thenReturn(false);

    // When & Then
    mockMvc.perform(post("/api/auth/refresh")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"refreshToken\":\"" + invalidToken + "\"}"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void refreshToken_shouldReturnUnauthorized_whenUserNotFound() throws Exception {
    // Given
    String refreshToken = "valid-refresh-token";

    when(jwtTokenProvider.isRefreshToken(refreshToken)).thenReturn(true);
    when(jwtTokenProvider.getUsernameFromToken(refreshToken)).thenReturn("johndoe");
    when(userRepository.findByUsername("johndoe")).thenReturn(Optional.empty());

    // When & Then
    mockMvc.perform(post("/api/auth/refresh")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
        .andExpect(status().isNotFound());
  }

  // ===== Get Current User Tests =====

  @Test
  void getCurrentUser_shouldReturnUserProfile_whenAuthenticated() throws Exception {
    // Given
    User user = createTestUser();
    when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(user));

    // Mock authentication
    SecurityContext securityContext = mock(SecurityContext.class);
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken("johndoe", null,
            List.of(new SimpleGrantedAuthority("ROLE_USER")));
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    // When & Then
    mockMvc.perform(get("/api/auth/me"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("johndoe"))
        .andExpect(jsonPath("$.email").value("john@example.com"))
        .andExpect(jsonPath("$.firstName").value("John"))
        .andExpect(jsonPath("$.lastName").value("Doe"));
  }

  @Test
  void getCurrentUser_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
    // Given
    SecurityContextHolder.clearContext();

    // When & Then
    mockMvc.perform(get("/api/auth/me"))
        .andExpect(status().isUnauthorized());
  }

  // ===== Logout Tests =====

  @Test
  void logout_shouldReturnOk_whenAuthenticated() throws Exception {
    // Given
    SecurityContext securityContext = mock(SecurityContext.class);
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken("johndoe", null, emptyList());
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);

    // When & Then
    mockMvc.perform(post("/api/auth/logout"))
        .andExpect(status().isOk());
  }
}

