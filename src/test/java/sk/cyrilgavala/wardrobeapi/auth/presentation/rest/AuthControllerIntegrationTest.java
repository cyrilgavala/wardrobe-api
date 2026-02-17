package sk.cyrilgavala.wardrobeapi.auth.presentation.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import sk.cyrilgavala.wardrobeapi.auth.domain.model.User;
import sk.cyrilgavala.wardrobeapi.auth.infrastructure.persistence.MongoUserRepository;
import sk.cyrilgavala.wardrobeapi.auth.infrastructure.security.JwtTokenProvider;
import sk.cyrilgavala.wardrobeapi.auth.presentation.dto.LoginRequest;
import sk.cyrilgavala.wardrobeapi.auth.presentation.dto.RefreshTokenRequest;
import sk.cyrilgavala.wardrobeapi.auth.presentation.dto.RegisterRequest;
import sk.cyrilgavala.wardrobeapi.shared.config.TestcontainersConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class AuthControllerIntegrationTest {

  @Autowired
  private WebApplicationContext context;

  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MongoUserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders
        .webAppContextSetup(context)
        .apply(springSecurity())
        .build();
    userRepository.deleteAll();
  }

  @AfterEach
  void tearDown() {
    userRepository.deleteAll();
  }

  @Test
  void registersNewUserSuccessfully() throws Exception {
    RegisterRequest request = new RegisterRequest(
        "Doe",
        "John",
        "Password123",
        "john@example.com",
        "johndoe"
    );

    mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.accessToken").exists())
        .andExpect(jsonPath("$.refreshToken").exists())
        .andExpect(jsonPath("$.tokenType").value("Bearer"))
        .andExpect(jsonPath("$.expiresIn").exists());

    assertThat(userRepository.findByUsername("johndoe")).isPresent();
  }

  @Test
  void returnsConflictWhenRegisteringWithDuplicateUsername() throws Exception {
    userRepository.save(User.create(
        "johndoe",
        "john@example.com",
        passwordEncoder.encode("Password123"),
        "John",
        "Doe"
    ));

    RegisterRequest request = new RegisterRequest(
        "Doe",
        "John",
        "Password123",
        "john2@example.com",
        "johndoe"
    );

    mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value("User with username johndoe already exists"));
  }

  @Test
  void returnsConflictWhenRegisteringWithDuplicateEmail() throws Exception {
    userRepository.save(User.create(
        "johndoe",
        "john@example.com",
        passwordEncoder.encode("Password123"),
        "John",
        "Doe"
    ));

    RegisterRequest request = new RegisterRequest(
        "Doe",
        "John",
        "Password123",
        "john@example.com",
        "johndoee"
    );

    mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value("User with email john@example.com already exists"));
  }

  @Test
  void loginSuccessfullyWithValidCredentials() throws Exception {
    userRepository.save(User.create(
        "johndoe",
        "john@example.com",
        passwordEncoder.encode("Password123"),
        "John",
        "Doe"
    ));

    LoginRequest request = new LoginRequest(
        "johndoe",
        "Password123"
    );

    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").exists())
        .andExpect(jsonPath("$.refreshToken").exists())
        .andExpect(jsonPath("$.tokenType").value("Bearer"));
  }

  @Test
  void returnsUnauthorizedWhenLoginWithIncorrectPassword() throws Exception {
    userRepository.save(User.create(
        "johndoe",
        "john@example.com",
        passwordEncoder.encode("Password123"),
        "John",
        "Doe"
    ));

    LoginRequest request = new LoginRequest(
        "johndoe",
        "Password123123"
    );

    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("Invalid password"));
  }

  @Test
  void returnsUnauthorizedWithNonExistingUsername() throws Exception {
    LoginRequest request = new LoginRequest(
        "nonexistent",
        "Password123"
    );

    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("Invalid username"));
  }

  @Test
  void retrievesCurrentUserProfileSuccessfully() throws Exception {
    userRepository.save(User.create(
        "johndoe",
        "john@example.com",
        passwordEncoder.encode("Password123"),
        "John",
        "Doe"
    ));

    LoginRequest loginRequest = new LoginRequest("johndoe", "Password123");
    MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andReturn();

    String accessToken = objectMapper.readTree(loginResult.getResponse().getContentAsString())
        .get("accessToken").asText();

    mockMvc.perform(get("/api/auth/me")
            .header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("johndoe"))
        .andExpect(jsonPath("$.email").value("john@example.com"));
  }

  @Test
  void returnsUnauthorizedWhenAccessingProfileWithoutToken() throws Exception {
    mockMvc.perform(get("/api/auth/me"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void returnsUnauthorizedWhenAccessingProfileWithInvalidToken() throws Exception {
    mockMvc.perform(get("/api/auth/me")
            .header("Authorization", "Bearer invalidtoken"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void returnsUnauthorizedWhenAccessingProfileWithExpiredToken() throws Exception {
    String expiredToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huZG9lIiwiZXhwIjoxNjE2MjM5MDIyfQ.invalidsignature";

    mockMvc.perform(get("/api/auth/me")
            .header("Authorization", "Bearer " + expiredToken))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void returnsUnauthorizedWhenAccessingProfileWithTokenOfDeletedUser() throws Exception {
    var savedUser = userRepository.save(User.create(
        "johndoe",
        "john@example.com",
        passwordEncoder.encode("Password123"),
        "John",
        "Doe"
    ));

    LoginRequest loginRequest = new LoginRequest("johndoe", "Password123");
    MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andReturn();

    String accessToken = objectMapper.readTree(loginResult.getResponse().getContentAsString())
        .get("accessToken").asText();

    userRepository.deleteById(savedUser.id());

    mockMvc.perform(get("/api/auth/me")
            .header("Authorization", "Bearer " + accessToken))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void returnsBadRequestForInvalidRegisterRequest() throws Exception {
    RegisterRequest request = new RegisterRequest(
        "",
        "",
        "short",
        "invalid-email",
        ""
    );

    mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void returnsBadRequestForInvalidLoginRequest() throws Exception {
    LoginRequest request = new LoginRequest(
        "",
        "short"
    );

    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void returnsBadRequestForInvalidRefreshTokenRequest() throws Exception {
    RefreshTokenRequest request = new RefreshTokenRequest("");

    mockMvc.perform(post("/api/auth/refresh")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void returnsUnauthorizedForInvalidRefreshToken() throws Exception {
    RefreshTokenRequest request = new RefreshTokenRequest("not-a-token");

    mockMvc.perform(post("/api/auth/refresh")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void returnsNotFoundWhenRefreshingTokenForUnknownUser() throws Exception {
    User phantomUser = User.builder()
        .id("ghost-id")
        .username("ghost")
        .email("ghost@example.com")
        .password("Password123")
        .firstName("Ghost")
        .lastName("User")
        .role(sk.cyrilgavala.wardrobeapi.auth.domain.model.UserRole.USER)
        .build();
    String refreshToken = jwtTokenProvider.generateRefreshToken(phantomUser);

    RefreshTokenRequest request = new RefreshTokenRequest(refreshToken);

    mockMvc.perform(post("/api/auth/refresh")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }
}
