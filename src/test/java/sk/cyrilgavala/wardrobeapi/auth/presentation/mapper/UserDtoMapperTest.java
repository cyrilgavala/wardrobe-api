package sk.cyrilgavala.wardrobeapi.auth.presentation.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.cyrilgavala.wardrobeapi.auth.application.command.LoginCommand;
import sk.cyrilgavala.wardrobeapi.auth.application.command.RegisterCommand;
import sk.cyrilgavala.wardrobeapi.auth.application.dto.UserDto;
import sk.cyrilgavala.wardrobeapi.auth.domain.model.UserRole;
import sk.cyrilgavala.wardrobeapi.auth.presentation.dto.LoginRequest;
import sk.cyrilgavala.wardrobeapi.auth.presentation.dto.RegisterRequest;
import sk.cyrilgavala.wardrobeapi.auth.presentation.dto.UserResponse;

class UserDtoMapperTest {

  private UserDtoMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new UserDtoMapper();
  }

  @Test
  void mapsRegisterRequestToCommandWithAllFields() {
    RegisterRequest request = new RegisterRequest(
        "Doe",
        "John",
        "password123",
        "john@example.com",
        "johndoe"
    );

    RegisterCommand result = mapper.toCommand(request);

    assertThat(result).isNotNull();
    assertThat(result.username()).isEqualTo("johndoe");
    assertThat(result.email()).isEqualTo("john@example.com");
    assertThat(result.password()).isEqualTo("password123");
    assertThat(result.firstName()).isEqualTo("John");
    assertThat(result.lastName()).isEqualTo("Doe");
  }

  @Test
  void mapsRegisterRequestWithSpecialCharacters() {
    RegisterRequest request = new RegisterRequest(
        "O'Brien",
        "Mary-Jane",
        "P@ssw0rd!",
        "user+tag@example.co.uk",
        "user.name-123"
    );

    RegisterCommand result = mapper.toCommand(request);

    assertThat(result.username()).isEqualTo("user.name-123");
    assertThat(result.email()).isEqualTo("user+tag@example.co.uk");
    assertThat(result.password()).isEqualTo("P@ssw0rd!");
    assertThat(result.firstName()).isEqualTo("Mary-Jane");
    assertThat(result.lastName()).isEqualTo("O'Brien");
  }

  @Test
  void mapsLoginRequestToCommand() {
    LoginRequest request = new LoginRequest(
        "johndoe",
        "password123"
    );

    LoginCommand result = mapper.toCommand(request);

    assertThat(result.username()).isEqualTo("johndoe");
    assertThat(result.password()).isEqualTo("password123");
  }

  @Test
  void mapsLoginRequestWithEmailAsUsername() {
    LoginRequest request = new LoginRequest(
        "user@example.com",
        "securePassword"
    );

    LoginCommand result = mapper.toCommand(request);

    assertThat(result.username()).isEqualTo("user@example.com");
    assertThat(result.password()).isEqualTo("securePassword");
  }

  @Test
  void mapsUserDtoToResponseWithAllFields() {
    UserDto userDto = UserDto.builder()
        .id("user123")
        .username("johndoe")
        .email("john@example.com")
        .firstName("John")
        .lastName("Doe")
        .role(UserRole.USER)
        .createdAt(Instant.parse("2024-01-01T10:00:00Z"))
        .lastLoginAt(Instant.parse("2024-01-05T08:00:00Z"))
        .build();

    UserResponse result = mapper.toResponse(userDto);

    assertThat(result.id()).isEqualTo("user123");
    assertThat(result.username()).isEqualTo("johndoe");
    assertThat(result.email()).isEqualTo("john@example.com");
    assertThat(result.firstName()).isEqualTo("John");
    assertThat(result.lastName()).isEqualTo("Doe");
    assertThat(result.role()).isEqualTo("USER");
    assertThat(result.createdAt()).isEqualTo(Instant.parse("2024-01-01T10:00:00Z"));
    assertThat(result.lastLoginAt()).isEqualTo(Instant.parse("2024-01-05T08:00:00Z"));
  }

  @Test
  void mapsUserDtoToResponseWithNullLastLoginAt() {
    UserDto userDto = UserDto.builder()
        .id("user456")
        .username("janedoe")
        .email("jane@example.com")
        .firstName("Jane")
        .lastName("Doe")
        .role(UserRole.USER)
        .createdAt(Instant.parse("2024-02-01T10:00:00Z"))
        .lastLoginAt(null)
        .build();

    UserResponse result = mapper.toResponse(userDto);

    assertThat(result.id()).isEqualTo("user456");
    assertThat(result.username()).isEqualTo("janedoe");
    assertThat(result.email()).isEqualTo("jane@example.com");
    assertThat(result.firstName()).isEqualTo("Jane");
    assertThat(result.lastName()).isEqualTo("Doe");
    assertThat(result.role()).isEqualTo("USER");
    assertThat(result.createdAt()).isEqualTo(Instant.parse("2024-02-01T10:00:00Z"));
    assertThat(result.lastLoginAt()).isNull();
  }

  @Test
  void mapsAdminUserDtoToResponse() {
    UserDto userDto = UserDto.builder()
        .id("admin1")
        .username("admin")
        .email("admin@example.com")
        .firstName("Admin")
        .lastName("User")
        .role(UserRole.ADMIN)
        .createdAt(Instant.parse("2024-01-01T00:00:00Z"))
        .lastLoginAt(Instant.parse("2024-01-10T12:00:00Z"))
        .build();

    UserResponse result = mapper.toResponse(userDto);

    assertThat(result.id()).isEqualTo("admin1");
    assertThat(result.username()).isEqualTo("admin");
    assertThat(result.email()).isEqualTo("admin@example.com");
    assertThat(result.firstName()).isEqualTo("Admin");
    assertThat(result.lastName()).isEqualTo("User");
    assertThat(result.role()).isEqualTo("ADMIN");
    assertThat(result.createdAt()).isEqualTo(Instant.parse("2024-01-01T00:00:00Z"));
    assertThat(result.lastLoginAt()).isEqualTo(Instant.parse("2024-01-10T12:00:00Z"));
  }

  @Test
  void returnsNullWhenUserDtoIsNull() {
    UserResponse result = mapper.toResponse(null);

    assertThat(result).isNull();
  }

  @Test
  void convertsRoleEnumToString() {
    UserDto userDto = UserDto.builder()
        .id("user789")
        .username("testuser")
        .email("test@example.com")
        .firstName("Test")
        .lastName("User")
        .role(UserRole.USER)
        .createdAt(Instant.now())
        .lastLoginAt(null)
        .build();

    UserResponse result = mapper.toResponse(userDto);

    assertThat(result.role()).isEqualTo("USER");
    assertThat(result.role()).isInstanceOf(String.class);
  }
}

