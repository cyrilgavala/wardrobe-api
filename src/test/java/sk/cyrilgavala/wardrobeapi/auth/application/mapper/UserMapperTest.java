package sk.cyrilgavala.wardrobeapi.auth.application.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sk.cyrilgavala.wardrobeapi.auth.application.dto.UserDto;
import sk.cyrilgavala.wardrobeapi.auth.domain.model.User;
import sk.cyrilgavala.wardrobeapi.auth.domain.model.UserRole;

class UserMapperTest {

  private UserMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new UserMapper();
  }

  @Test
  void mapsUserToDtoWithAllFields() {
    User user = User.builder()
        .id("user123")
        .username("johndoe")
        .email("john@example.com")
        .password("encoded_password")
        .firstName("John")
        .lastName("Doe")
        .role(UserRole.USER)
        .createdAt(Instant.parse("2024-01-01T10:00:00Z"))
        .updatedAt(Instant.parse("2024-01-02T15:30:00Z"))
        .lastLoginAt(Instant.parse("2024-01-05T08:00:00Z"))
        .build();

    UserDto result = mapper.toDto(user);

    assertThat(result.id()).isEqualTo("user123");
    assertThat(result.username()).isEqualTo("johndoe");
    assertThat(result.email()).isEqualTo("john@example.com");
    assertThat(result.firstName()).isEqualTo("John");
    assertThat(result.lastName()).isEqualTo("Doe");
    assertThat(result.role()).isEqualTo(UserRole.USER);
    assertThat(result.createdAt()).isEqualTo(Instant.parse("2024-01-01T10:00:00Z"));
    assertThat(result.lastLoginAt()).isEqualTo(Instant.parse("2024-01-05T08:00:00Z"));
  }

  @Test
  void mapsUserToDtoWithNullLastLoginAt() {
    User user = User.builder()
        .id("user456")
        .username("janedoe")
        .email("jane@example.com")
        .password("encoded_password")
        .firstName("Jane")
        .lastName("Doe")
        .role(UserRole.USER)
        .createdAt(Instant.parse("2024-02-01T10:00:00Z"))
        .updatedAt(Instant.parse("2024-02-01T10:00:00Z"))
        .lastLoginAt(null)
        .build();

    UserDto result = mapper.toDto(user);

    assertThat(result.id()).isEqualTo("user456");
    assertThat(result.username()).isEqualTo("janedoe");
    assertThat(result.email()).isEqualTo("jane@example.com");
    assertThat(result.firstName()).isEqualTo("Jane");
    assertThat(result.lastName()).isEqualTo("Doe");
    assertThat(result.role()).isEqualTo(UserRole.USER);
    assertThat(result.createdAt()).isEqualTo(Instant.parse("2024-02-01T10:00:00Z"));
    assertThat(result.lastLoginAt()).isNull();
  }

  @Test
  void mapsAdminUserToDto() {
    User user = User.builder()
        .id("admin1")
        .username("admin")
        .email("admin@example.com")
        .password("encoded_password")
        .firstName("Admin")
        .lastName("User")
        .role(UserRole.ADMIN)
        .createdAt(Instant.parse("2024-01-01T00:00:00Z"))
        .updatedAt(Instant.parse("2024-01-01T00:00:00Z"))
        .lastLoginAt(Instant.parse("2024-01-10T12:00:00Z"))
        .build();

    UserDto result = mapper.toDto(user);

    assertThat(result.id()).isEqualTo("admin1");
    assertThat(result.username()).isEqualTo("admin");
    assertThat(result.email()).isEqualTo("admin@example.com");
    assertThat(result.firstName()).isEqualTo("Admin");
    assertThat(result.lastName()).isEqualTo("User");
    assertThat(result.role()).isEqualTo(UserRole.ADMIN);
    assertThat(result.createdAt()).isEqualTo(Instant.parse("2024-01-01T00:00:00Z"));
    assertThat(result.lastLoginAt()).isEqualTo(Instant.parse("2024-01-10T12:00:00Z"));
  }

  @Test
  void returnsNullWhenUserIsNull() {
    UserDto result = mapper.toDto(null);

    assertThat(result).isNull();
  }

  @Test
  void excludesPasswordFromDto() {
    User user = User.builder()
        .id("user789")
        .username("testuser")
        .email("test@example.com")
        .password("super_secret_password")
        .firstName("Test")
        .lastName("User")
        .role(UserRole.USER)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .lastLoginAt(null)
        .build();

    UserDto result = mapper.toDto(user);

    assertThat(result.id()).isEqualTo("user789");
    assertThat(result.username()).isEqualTo("testuser");
    assertThat(result).hasOnlyFields("id", "username", "email", "firstName", "lastName", "role",
        "createdAt", "lastLoginAt");
  }
}

