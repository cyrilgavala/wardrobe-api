package sk.cyrilgavala.wardrobeapi.auth.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sk.cyrilgavala.wardrobeapi.auth.domain.model.User;
import sk.cyrilgavala.wardrobeapi.auth.domain.model.UserRole;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {

  @Mock
  private MongoUserRepository mongoUserRepository;

  @InjectMocks
  private UserRepositoryImpl userRepository;

  private User testUser;

  @BeforeEach
  void setUp() {
    Instant now = Instant.now();
    testUser = new User(
        "user-id-123",
        "johndoe",
        "john@example.com",
        "John",
        "Doe",
        "encodedPassword",
        UserRole.USER,
        now,
        now,
        null
    );
  }

  @Test
  void save_ShouldDelegateToMongoRepository() {
    // Given
    when(mongoUserRepository.save(any(User.class))).thenReturn(testUser);

    // When
    User result = userRepository.save(testUser);

    // Then
    assertThat(result).isEqualTo(testUser);
    verify(mongoUserRepository).save(testUser);
  }

  @Test
  void findById_ShouldDelegateToMongoRepository() {
    // Given
    String userId = "user-id-123";
    when(mongoUserRepository.findById(anyString())).thenReturn(Optional.of(testUser));

    // When
    Optional<User> result = userRepository.findById(userId);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(testUser);
    verify(mongoUserRepository).findById(userId);
  }

  @Test
  void findById_WhenUserNotFound_ShouldReturnEmptyOptional() {
    // Given
    String userId = "non-existent-id";
    when(mongoUserRepository.findById(anyString())).thenReturn(Optional.empty());

    // When
    Optional<User> result = userRepository.findById(userId);

    // Then
    assertThat(result).isEmpty();
    verify(mongoUserRepository).findById(userId);
  }

  @Test
  void findByUsername_ShouldDelegateToMongoRepository() {
    // Given
    String username = "johndoe";
    when(mongoUserRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));

    // When
    Optional<User> result = userRepository.findByUsername(username);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(testUser);
    verify(mongoUserRepository).findByUsername(username);
  }

  @Test
  void findByUsername_WhenUserNotFound_ShouldReturnEmptyOptional() {
    // Given
    String username = "nonexistent";
    when(mongoUserRepository.findByUsername(anyString())).thenReturn(Optional.empty());

    // When
    Optional<User> result = userRepository.findByUsername(username);

    // Then
    assertThat(result).isEmpty();
    verify(mongoUserRepository).findByUsername(username);
  }

  @Test
  void findByEmail_ShouldDelegateToMongoRepository() {
    // Given
    String email = "john@example.com";
    when(mongoUserRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

    // When
    Optional<User> result = userRepository.findByEmail(email);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(testUser);
    verify(mongoUserRepository).findByEmail(email);
  }

  @Test
  void findByEmail_WhenUserNotFound_ShouldReturnEmptyOptional() {
    // Given
    String email = "nonexistent@example.com";
    when(mongoUserRepository.findByEmail(anyString())).thenReturn(Optional.empty());

    // When
    Optional<User> result = userRepository.findByEmail(email);

    // Then
    assertThat(result).isEmpty();
    verify(mongoUserRepository).findByEmail(email);
  }

  @Test
  void existsByUsername_WhenExists_ShouldReturnTrue() {
    // Given
    String username = "johndoe";
    when(mongoUserRepository.existsByUsername(anyString())).thenReturn(true);

    // When
    boolean result = userRepository.existsByUsername(username);

    // Then
    assertThat(result).isTrue();
    verify(mongoUserRepository).existsByUsername(username);
  }

  @Test
  void existsByUsername_WhenNotExists_ShouldReturnFalse() {
    // Given
    String username = "nonexistent";
    when(mongoUserRepository.existsByUsername(anyString())).thenReturn(false);

    // When
    boolean result = userRepository.existsByUsername(username);

    // Then
    assertThat(result).isFalse();
    verify(mongoUserRepository).existsByUsername(username);
  }

  @Test
  void existsByEmail_WhenExists_ShouldReturnTrue() {
    // Given
    String email = "john@example.com";
    when(mongoUserRepository.existsByEmail(anyString())).thenReturn(true);

    // When
    boolean result = userRepository.existsByEmail(email);

    // Then
    assertThat(result).isTrue();
    verify(mongoUserRepository).existsByEmail(email);
  }

  @Test
  void existsByEmail_WhenNotExists_ShouldReturnFalse() {
    // Given
    String email = "nonexistent@example.com";
    when(mongoUserRepository.existsByEmail(anyString())).thenReturn(false);

    // When
    boolean result = userRepository.existsByEmail(email);

    // Then
    assertThat(result).isFalse();
    verify(mongoUserRepository).existsByEmail(email);
  }

  @Test
  void deleteById_ShouldDelegateToMongoRepository() {
    // Given
    String userId = "user-id-123";

    // When
    userRepository.deleteById(userId);

    // Then
    verify(mongoUserRepository).deleteById(userId);
  }
}

