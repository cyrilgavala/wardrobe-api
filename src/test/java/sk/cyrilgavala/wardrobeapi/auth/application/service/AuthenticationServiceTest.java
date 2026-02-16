package sk.cyrilgavala.wardrobeapi.auth.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import sk.cyrilgavala.wardrobeapi.auth.application.command.LoginCommand;
import sk.cyrilgavala.wardrobeapi.auth.application.command.RegisterUserCommand;
import sk.cyrilgavala.wardrobeapi.auth.application.dto.UserDto;
import sk.cyrilgavala.wardrobeapi.auth.application.mapper.UserMapper;
import sk.cyrilgavala.wardrobeapi.auth.domain.exception.DuplicateUserException;
import sk.cyrilgavala.wardrobeapi.auth.domain.exception.InvalidCredentialsException;
import sk.cyrilgavala.wardrobeapi.auth.domain.exception.UserNotFoundException;
import sk.cyrilgavala.wardrobeapi.auth.domain.model.User;
import sk.cyrilgavala.wardrobeapi.auth.domain.model.UserRole;
import sk.cyrilgavala.wardrobeapi.auth.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private AuthenticationService service;

  @Test
  void registersNewUserSuccessfully() {
    RegisterUserCommand command = RegisterUserCommand.builder()
        .username("johndoe")
        .email("john@example.com")
        .password("password123")
        .firstName("John")
        .lastName("Doe")
        .build();
    User savedUser = User.builder()
        .id("user123")
        .username("johndoe")
        .email("john@example.com")
        .password("encoded_password")
        .firstName("John")
        .lastName("Doe")
        .role(UserRole.USER)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();
    UserDto userDto = UserDto.builder()
        .id("user123")
        .username("johndoe")
        .email("john@example.com")
        .firstName("John")
        .lastName("Doe")
        .role(UserRole.USER)
        .createdAt(Instant.now())
        .build();

    when(userRepository.existsByUsername("johndoe")).thenReturn(false);
    when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
    when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
    when(userRepository.save(any(User.class))).thenReturn(savedUser);
    when(userMapper.toDto(savedUser)).thenReturn(userDto);

    UserDto result = service.register(command);

    assertThat(result).isEqualTo(userDto);
    verify(userRepository).existsByUsername("johndoe");
    verify(userRepository).existsByEmail("john@example.com");
    verify(passwordEncoder).encode("password123");
    verify(userRepository).save(any(User.class));
  }

  @Test
  void throwsExceptionWhenUsernameAlreadyExists() {
    RegisterUserCommand command = RegisterUserCommand.builder()
        .username("existinguser")
        .email("new@example.com")
        .password("password123")
        .firstName("New")
        .lastName("User")
        .build();

    when(userRepository.existsByUsername("existinguser")).thenReturn(true);

    assertThatThrownBy(() -> service.register(command))
        .isInstanceOf(DuplicateUserException.class)
        .hasMessageContaining("existinguser");
  }

  @Test
  void throwsExceptionWhenEmailAlreadyExists() {
    RegisterUserCommand command = RegisterUserCommand.builder()
        .username("newuser")
        .email("existing@example.com")
        .password("password123")
        .firstName("New")
        .lastName("User")
        .build();

    when(userRepository.existsByUsername("newuser")).thenReturn(false);
    when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

    assertThatThrownBy(() -> service.register(command))
        .isInstanceOf(DuplicateUserException.class)
        .hasMessageContaining("existing@example.com");
  }

  @Test
  void encodesPasswordDuringRegistration() {
    RegisterUserCommand command = RegisterUserCommand.builder()
        .username("johndoe")
        .email("john@example.com")
        .password("plain_password")
        .firstName("John")
        .lastName("Doe")
        .build();
    User savedUser = User.builder()
        .id("user123")
        .username("johndoe")
        .email("john@example.com")
        .password("super_encoded_password")
        .firstName("John")
        .lastName("Doe")
        .role(UserRole.USER)
        .build();

    when(userRepository.existsByUsername("johndoe")).thenReturn(false);
    when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
    when(passwordEncoder.encode("plain_password")).thenReturn("super_encoded_password");
    when(userRepository.save(any(User.class))).thenReturn(savedUser);
    when(userMapper.toDto(any(User.class))).thenReturn(UserDto.builder().build());

    service.register(command);

    verify(passwordEncoder).encode("plain_password");
  }

  @Test
  void authenticatesUserSuccessfully() {
    LoginCommand command = LoginCommand.builder()
        .username("johndoe")
        .password("password123")
        .build();
    User existingUser = User.builder()
        .id("user123")
        .username("johndoe")
        .email("john@example.com")
        .password("encoded_password")
        .firstName("John")
        .lastName("Doe")
        .role(UserRole.USER)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();
    User updatedUser = User.builder()
        .id("user123")
        .username("johndoe")
        .email("john@example.com")
        .password("encoded_password")
        .firstName("John")
        .lastName("Doe")
        .role(UserRole.USER)
        .createdAt(existingUser.createdAt())
        .updatedAt(Instant.now())
        .lastLoginAt(Instant.now())
        .build();
    UserDto userDto = UserDto.builder()
        .id("user123")
        .username("johndoe")
        .email("john@example.com")
        .firstName("John")
        .lastName("Doe")
        .role(UserRole.USER)
        .createdAt(Instant.now())
        .lastLoginAt(Instant.now())
        .build();

    when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(existingUser));
    when(passwordEncoder.matches("password123", "encoded_password")).thenReturn(true);
    when(userRepository.save(any(User.class))).thenReturn(updatedUser);
    when(userMapper.toDto(updatedUser)).thenReturn(userDto);

    UserDto result = service.login(command);

    assertThat(result).isEqualTo(userDto);
    verify(userRepository).findByUsername("johndoe");
    verify(passwordEncoder).matches("password123", "encoded_password");
    verify(userRepository).save(any(User.class));
  }

  @Test
  void throwsExceptionWhenUserNotFoundDuringLogin() {
    LoginCommand command = LoginCommand.builder()
        .username("nonexistent")
        .password("password123")
        .build();

    when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.login(command))
        .isInstanceOf(InvalidCredentialsException.class)
        .hasMessageContaining("Invalid username or password");
  }

  @Test
  void throwsExceptionWhenPasswordIsIncorrect() {
    LoginCommand command = LoginCommand.builder()
        .username("johndoe")
        .password("wrong_password")
        .build();
    User existingUser = User.builder()
        .id("user123")
        .username("johndoe")
        .email("john@example.com")
        .password("encoded_password")
        .role(UserRole.USER)
        .build();

    when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(existingUser));
    when(passwordEncoder.matches("wrong_password", "encoded_password")).thenReturn(false);

    assertThatThrownBy(() -> service.login(command))
        .isInstanceOf(InvalidCredentialsException.class)
        .hasMessageContaining("Invalid username or password");
  }

  @Test
  void updatesLastLoginAtAfterSuccessfulLogin() {
    LoginCommand command = LoginCommand.builder()
        .username("johndoe")
        .password("password123")
        .build();
    User existingUser = User.builder()
        .id("user123")
        .username("johndoe")
        .password("encoded_password")
        .role(UserRole.USER)
        .lastLoginAt(null)
        .build();

    when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(existingUser));
    when(passwordEncoder.matches("password123", "encoded_password")).thenReturn(true);
    when(userRepository.save(any(User.class))).thenReturn(existingUser);
    when(userMapper.toDto(any(User.class))).thenReturn(UserDto.builder().build());

    service.login(command);

    verify(userRepository).save(any(User.class));
  }

  @Test
  void retrievesUserByUsernameSuccessfully() {
    User user = User.builder()
        .id("user123")
        .username("johndoe")
        .email("john@example.com")
        .role(UserRole.USER)
        .build();
    UserDto userDto = UserDto.builder()
        .id("user123")
        .username("johndoe")
        .email("john@example.com")
        .role(UserRole.USER)
        .build();

    when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(user));
    when(userMapper.toDto(user)).thenReturn(userDto);

    UserDto result = service.getUserByUsername("johndoe");

    assertThat(result).isEqualTo(userDto);
    verify(userRepository).findByUsername("johndoe");
  }

  @Test
  void throwsExceptionWhenUserNotFoundByUsername() {
    when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.getUserByUsername("unknown"))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessageContaining("unknown");
  }
}

