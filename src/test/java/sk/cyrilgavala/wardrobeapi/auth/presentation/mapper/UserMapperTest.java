package sk.cyrilgavala.wardrobeapi.auth.presentation.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.cyrilgavala.wardrobeapi.auth.application.command.LoginCommand;
import sk.cyrilgavala.wardrobeapi.auth.application.command.RegisterUserCommand;
import sk.cyrilgavala.wardrobeapi.auth.domain.model.User;
import sk.cyrilgavala.wardrobeapi.auth.domain.model.UserRole;
import sk.cyrilgavala.wardrobeapi.auth.presentation.dto.LoginRequest;
import sk.cyrilgavala.wardrobeapi.auth.presentation.dto.RegisterRequest;
import sk.cyrilgavala.wardrobeapi.auth.presentation.dto.UserResponse;

class UserMapperTest {

  private UserMapper userMapper;

  @BeforeEach
  void setUp() {
    userMapper = new UserMapper();
  }

  @Test
  void toCommand_shouldMapRegisterRequestToCommand() {
    // Given
    RegisterRequest request = RegisterRequest.builder()
        .username("johndoe")
        .email("john@example.com")
        .password("password123")
        .firstName("John")
        .lastName("Doe")
        .build();

    // When
    RegisterUserCommand command = userMapper.toCommand(request);

    // Then
    assertThat(command).isNotNull();
    assertThat(command.username()).isEqualTo("johndoe");
    assertThat(command.email()).isEqualTo("john@example.com");
    assertThat(command.password()).isEqualTo("password123");
    assertThat(command.firstName()).isEqualTo("John");
    assertThat(command.lastName()).isEqualTo("Doe");
  }

  @Test
  void toCommand_shouldMapLoginRequestToCommand() {
    // Given
    LoginRequest request = new LoginRequest("johndoe", "password123");

    // When
    LoginCommand command = userMapper.toCommand(request);

    // Then
    assertThat(command).isNotNull();
    assertThat(command.username()).isEqualTo("johndoe");
    assertThat(command.password()).isEqualTo("password123");
  }

  @Test
  void toResponse_shouldMapUserToUserResponse() {
    // Given
    User user = User.create(
        "johndoe",
        "john@example.com",
        "encodedPassword",
        "John",
        "Doe"
    );

    // When
    UserResponse response = userMapper.toResponse(user);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.username()).isEqualTo("johndoe");
    assertThat(response.email()).isEqualTo("john@example.com");
    assertThat(response.firstName()).isEqualTo("John");
    assertThat(response.lastName()).isEqualTo("Doe");
    assertThat(response.role()).isEqualTo(UserRole.USER);
    assertThat(response.createdAt()).isNotNull();
    assertThat(response.lastLoginAt()).isNull();
  }

  @Test
  void toResponse_shouldNotIncludePassword() {
    // Given
    User user = User.create(
        "johndoe",
        "john@example.com",
        "encodedPassword",
        "John",
        "Doe"
    );

    // When
    UserResponse response = userMapper.toResponse(user);

    // Then
    // UserResponse shouldn't have a password field at all
    assertThat(response.toString()).doesNotContain("encodedPassword");
    assertThat(response.toString()).doesNotContain("password");
  }

  @Test
  void toResponse_shouldIncludeLastLoginAt_whenPresent() {
    // Given
    User user = User.create(
        "johndoe",
        "john@example.com",
        "encodedPassword",
        "John",
        "Doe"
    ).recordLogin();

    // When
    UserResponse response = userMapper.toResponse(user);

    // Then
    assertThat(response.lastLoginAt()).isNotNull();
  }

  @Test
  void toResponse_shouldHandleAdminRole() {
    // Given
    User user = User.create(
        "admin",
        "admin@example.com",
        "encodedPassword",
        "Admin",
        "User"
    ).promoteToAdmin();

    // When
    UserResponse response = userMapper.toResponse(user);

    // Then
    assertThat(response.role()).isEqualTo(UserRole.ADMIN);
  }
}

