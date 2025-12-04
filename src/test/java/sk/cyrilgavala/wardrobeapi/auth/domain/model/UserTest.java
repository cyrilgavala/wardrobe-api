package sk.cyrilgavala.wardrobeapi.auth.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class UserTest {

  @Test
  void create_shouldCreateUserWithDefaultValues() {
    // Given
    String username = "johndoe";
    String email = "john@example.com";
    String password = "encodedPassword";
    String firstName = "John";
    String lastName = "Doe";

    // When
    User user = User.create(username, email, password, firstName, lastName);

    // Then
    assertThat(user.username()).isEqualTo(username);
    assertThat(user.email()).isEqualTo(email);
    assertThat(user.password()).isEqualTo(password);
    assertThat(user.firstName()).isEqualTo(firstName);
    assertThat(user.lastName()).isEqualTo(lastName);
    assertThat(user.role()).isEqualTo(UserRole.USER);
    assertThat(user.createdAt()).isNotNull();
    assertThat(user.updatedAt()).isNotNull();
    assertThat(user.lastLoginAt()).isNull();
  }

  @Test
  void recordLogin_shouldUpdateLastLoginTimestamp() {
    // Given
    User user = User.create("johndoe", "john@example.com", "password", "John", "Doe");
    assertThat(user.lastLoginAt()).isNull();

    // When
    User loggedInUser = user.recordLogin();

    // Then
    assertThat(loggedInUser.lastLoginAt()).isNotNull();
    assertThat(loggedInUser.lastLoginAt()).isBeforeOrEqualTo(Instant.now());
  }

  @Test
  void promoteToAdmin_shouldChangeRoleToAdmin() {
    // Given
    User user = User.create("johndoe", "john@example.com", "password", "John", "Doe");
    assertThat(user.role()).isEqualTo(UserRole.USER);

    // When
    User adminUser = user.promoteToAdmin();

    // Then
    assertThat(adminUser.role()).isEqualTo(UserRole.ADMIN);
    assertThat(adminUser.updatedAt()).isAfter(user.updatedAt());
  }

  @Test
  void recordLogin_shouldNotModifyOtherFields() {
    // Given
    User user = User.create("johndoe", "john@example.com", "password", "John", "Doe");

    // When
    User loggedInUser = user.recordLogin();

    // Then
    assertThat(loggedInUser.username()).isEqualTo(user.username());
    assertThat(loggedInUser.email()).isEqualTo(user.email());
    assertThat(loggedInUser.password()).isEqualTo(user.password());
    assertThat(loggedInUser.firstName()).isEqualTo(user.firstName());
    assertThat(loggedInUser.lastName()).isEqualTo(user.lastName());
    assertThat(loggedInUser.role()).isEqualTo(user.role());
  }
}

