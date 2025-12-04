package sk.cyrilgavala.wardrobeapi.auth.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import sk.cyrilgavala.wardrobeapi.auth.application.command.LoginCommand;
import sk.cyrilgavala.wardrobeapi.auth.application.command.RegisterUserCommand;
import sk.cyrilgavala.wardrobeapi.auth.domain.exception.DuplicateUserException;
import sk.cyrilgavala.wardrobeapi.auth.domain.exception.InvalidCredentialsException;
import sk.cyrilgavala.wardrobeapi.auth.domain.model.User;
import sk.cyrilgavala.wardrobeapi.auth.domain.model.UserRole;
import sk.cyrilgavala.wardrobeapi.auth.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private AuthenticationService authenticationService;

  private RegisterUserCommand registerCommand;
  private LoginCommand loginCommand;
  private User testUser;

  @BeforeEach
  void setUp() {
    registerCommand = RegisterUserCommand.builder()
        .username("johndoe")
        .email("john@example.com")
        .password("plainPassword")
        .firstName("John")
        .lastName("Doe")
        .build();

    loginCommand = LoginCommand.builder()
        .username("johndoe")
        .password("plainPassword")
        .build();

    testUser = User.create(
        "johndoe",
        "john@example.com",
        "encodedPassword",
        "John",
        "Doe"
    );
  }

  // ===== Registration Tests =====

  @Test
  void register_shouldCreateUserSuccessfully() {
    // Given
    when(userRepository.existsByUsername(anyString())).thenReturn(false);
    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
    when(userRepository.save(any(User.class))).thenReturn(testUser);

    // When
    User result = authenticationService.register(registerCommand);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.username()).isEqualTo("johndoe");
    assertThat(result.email()).isEqualTo("john@example.com");
    verify(userRepository).existsByUsername("johndoe");
    verify(userRepository).existsByEmail("john@example.com");
    verify(passwordEncoder).encode("plainPassword");
    verify(userRepository).save(any(User.class));
  }

  @Test
  void register_shouldThrowException_whenUsernameAlreadyExists() {
    // Given
    when(userRepository.existsByUsername("johndoe")).thenReturn(true);

    // When & Then
    assertThatThrownBy(() -> authenticationService.register(registerCommand))
        .isInstanceOf(DuplicateUserException.class)
        .hasMessageContaining("johndoe");

    verify(userRepository).existsByUsername("johndoe");
    verify(userRepository, never()).existsByEmail(anyString());
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void register_shouldThrowException_whenEmailAlreadyExists() {
    // Given
    when(userRepository.existsByUsername(anyString())).thenReturn(false);
    when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

    // When & Then
    assertThatThrownBy(() -> authenticationService.register(registerCommand))
        .isInstanceOf(DuplicateUserException.class)
        .hasMessageContaining("john@example.com");

    verify(userRepository).existsByUsername("johndoe");
    verify(userRepository).existsByEmail("john@example.com");
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void register_shouldEncodePassword() {
    // Given
    when(userRepository.existsByUsername(anyString())).thenReturn(false);
    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    User result = authenticationService.register(registerCommand);

    // Then
    assertThat(result.password()).isEqualTo("encodedPassword");
    verify(passwordEncoder).encode("plainPassword");
  }

  // ===== Login Tests =====

  @Test
  void login_shouldAuthenticateSuccessfully_withValidCredentials() {
    // Given
    when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));
    when(passwordEncoder.matches("plainPassword", "encodedPassword")).thenReturn(true);
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    User result = authenticationService.login(loginCommand);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.username()).isEqualTo("johndoe");
    assertThat(result.lastLoginAt()).isNotNull();
    verify(userRepository).findByUsername("johndoe");
    verify(passwordEncoder).matches("plainPassword", "encodedPassword");
    verify(userRepository).save(any(User.class));
  }

  @Test
  void login_shouldThrowException_whenUserNotFound() {
    // Given
    when(userRepository.findByUsername("johndoe")).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> authenticationService.login(loginCommand))
        .isInstanceOf(InvalidCredentialsException.class)
        .hasMessageContaining("Invalid username or password");

    verify(userRepository).findByUsername("johndoe");
    verify(passwordEncoder, never()).matches(anyString(), anyString());
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void login_shouldThrowException_whenPasswordIsInvalid() {
    // Given
    when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));
    when(passwordEncoder.matches("plainPassword", "encodedPassword")).thenReturn(false);

    // When & Then
    assertThatThrownBy(() -> authenticationService.login(loginCommand))
        .isInstanceOf(InvalidCredentialsException.class)
        .hasMessageContaining("Invalid username or password");

    verify(userRepository).findByUsername("johndoe");
    verify(passwordEncoder).matches("plainPassword", "encodedPassword");
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void login_shouldRecordLoginTimestamp() {
    // Given
    when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));
    when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    User result = authenticationService.login(loginCommand);

    // Then
    assertThat(result.lastLoginAt()).isNotNull();
    verify(userRepository).save(any(User.class));
  }

  @Test
  void register_shouldSetUserRoleByDefault() {
    // Given
    when(userRepository.existsByUsername(anyString())).thenReturn(false);
    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    User result = authenticationService.register(registerCommand);

    // Then
    assertThat(result.role()).isEqualTo(UserRole.USER);
  }
}

