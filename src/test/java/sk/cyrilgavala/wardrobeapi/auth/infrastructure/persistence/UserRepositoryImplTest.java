package sk.cyrilgavala.wardrobeapi.auth.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
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
  private UserRepositoryImpl repository;

  @Test
  void savesUserSuccessfully() {
    User user = User.builder()
        .id("user123")
        .username("johndoe")
        .email("john@example.com")
        .password("encoded_password")
        .firstName("John")
        .lastName("Doe")
        .role(UserRole.USER)
        .build();

    when(mongoUserRepository.save(user)).thenReturn(user);

    User result = repository.save(user);

    assertThat(result).isEqualTo(user);
    verify(mongoUserRepository).save(user);
  }

  @Test
  void savesNewUserWithoutId() {
    User newUser = User.builder()
        .username("janedoe")
        .email("jane@example.com")
        .password("encoded_password")
        .firstName("Jane")
        .lastName("Doe")
        .role(UserRole.USER)
        .build();
    User savedUser = User.builder()
        .id("generatedId")
        .username("janedoe")
        .email("jane@example.com")
        .password("encoded_password")
        .firstName("Jane")
        .lastName("Doe")
        .role(UserRole.USER)
        .build();

    when(mongoUserRepository.save(newUser)).thenReturn(savedUser);

    User result = repository.save(newUser);

    assertThat(result).isEqualTo(savedUser);
  }

  @Test
  void findsUserByIdSuccessfully() {
    User user = User.builder()
        .id("user123")
        .username("johndoe")
        .email("john@example.com")
        .role(UserRole.USER)
        .build();

    when(mongoUserRepository.findById("user123")).thenReturn(Optional.of(user));

    Optional<User> result = repository.findById("user123");

    assertThat(result).contains(user);
    verify(mongoUserRepository).findById("user123");
  }

  @Test
  void returnsEmptyWhenUserNotFoundById() {
    when(mongoUserRepository.findById("nonexistent")).thenReturn(Optional.empty());

    Optional<User> result = repository.findById("nonexistent");

    assertThat(result).isEmpty();
  }

  @Test
  void findsUserByUsernameSuccessfully() {
    User user = User.builder()
        .id("user456")
        .username("janedoe")
        .email("jane@example.com")
        .role(UserRole.USER)
        .build();

    when(mongoUserRepository.findByUsername("janedoe")).thenReturn(Optional.of(user));

    Optional<User> result = repository.findByUsername("janedoe");

    assertThat(result).contains(user);
    verify(mongoUserRepository).findByUsername("janedoe");
  }

  @Test
  void returnsEmptyWhenUserNotFoundByUsername() {
    when(mongoUserRepository.findByUsername("unknown")).thenReturn(Optional.empty());

    Optional<User> result = repository.findByUsername("unknown");

    assertThat(result).isEmpty();
  }

  @Test
  void findsUserByEmailSuccessfully() {
    User user = User.builder()
        .id("user789")
        .username("testuser")
        .email("test@example.com")
        .role(UserRole.USER)
        .build();

    when(mongoUserRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

    Optional<User> result = repository.findByEmail("test@example.com");

    assertThat(result).contains(user);
    verify(mongoUserRepository).findByEmail("test@example.com");
  }

  @Test
  void returnsEmptyWhenUserNotFoundByEmail() {
    when(mongoUserRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

    Optional<User> result = repository.findByEmail("unknown@example.com");

    assertThat(result).isEmpty();
  }

  @Test
  void returnsTrueWhenUsernameExists() {
    when(mongoUserRepository.existsByUsername("existinguser")).thenReturn(true);

    boolean result = repository.existsByUsername("existinguser");

    assertThat(result).isTrue();
    verify(mongoUserRepository).existsByUsername("existinguser");
  }

  @Test
  void returnsFalseWhenUsernameDoesNotExist() {
    when(mongoUserRepository.existsByUsername("newuser")).thenReturn(false);

    boolean result = repository.existsByUsername("newuser");

    assertThat(result).isFalse();
  }

  @Test
  void returnsTrueWhenEmailExists() {
    when(mongoUserRepository.existsByEmail("existing@example.com")).thenReturn(true);

    boolean result = repository.existsByEmail("existing@example.com");

    assertThat(result).isTrue();
    verify(mongoUserRepository).existsByEmail("existing@example.com");
  }

  @Test
  void returnsFalseWhenEmailDoesNotExist() {
    when(mongoUserRepository.existsByEmail("new@example.com")).thenReturn(false);

    boolean result = repository.existsByEmail("new@example.com");

    assertThat(result).isFalse();
  }

  @Test
  void deletesUserById() {
    repository.deleteById("user123");

    verify(mongoUserRepository).deleteById("user123");
  }

  @Test
  void delegatesSaveToMongoRepository() {
    User user = User.builder()
        .id("user999")
        .username("testuser")
        .email("test@example.com")
        .role(UserRole.ADMIN)
        .build();

    when(mongoUserRepository.save(user)).thenReturn(user);

    repository.save(user);

    verify(mongoUserRepository).save(user);
  }
}

