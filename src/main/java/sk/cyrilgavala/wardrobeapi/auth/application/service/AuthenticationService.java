package sk.cyrilgavala.wardrobeapi.auth.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.cyrilgavala.wardrobeapi.auth.application.command.LoginCommand;
import sk.cyrilgavala.wardrobeapi.auth.application.command.RegisterUserCommand;
import sk.cyrilgavala.wardrobeapi.auth.application.dto.UserDto;
import sk.cyrilgavala.wardrobeapi.auth.application.mapper.UserDtoMapper;
import sk.cyrilgavala.wardrobeapi.auth.domain.exception.DuplicateUserException;
import sk.cyrilgavala.wardrobeapi.auth.domain.exception.InvalidCredentialsException;
import sk.cyrilgavala.wardrobeapi.auth.domain.exception.UserNotFoundException;
import sk.cyrilgavala.wardrobeapi.auth.domain.model.User;
import sk.cyrilgavala.wardrobeapi.auth.domain.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserDtoMapper userDtoMapper;

  @Transactional
  public UserDto register(RegisterUserCommand command) {
    log.info("Registering new user with username: {}", command.username());

    // Validate username uniqueness
    if (userRepository.existsByUsername(command.username())) {
      log.warn("Registration failed: username already exists - {}", command.username());
      throw DuplicateUserException.withUsername(command.username());
    }

    // Validate email uniqueness
    if (userRepository.existsByEmail(command.email())) {
      log.warn("Registration failed: email already exists - {}", command.email());
      throw DuplicateUserException.withEmail(command.email());
    }

    // Encode password
    String encodedPassword = passwordEncoder.encode(command.password());

    // Create user
    User user = User.create(
        command.username(),
        command.email(),
        encodedPassword,
        command.firstName(),
        command.lastName()
    );

    // Save user
    User savedUser = userRepository.save(user);

    log.info("User registered successfully with id: {}", savedUser.id());

    return userDtoMapper.toDto(savedUser);
  }

  @Transactional
  public UserDto login(LoginCommand command) {
    log.info("Login attempt for username: {}", command.username());

    // Find user by username
    User user = userRepository.findByUsername(command.username())
        .orElseThrow(() -> {
          log.warn("Login failed: user not found - {}", command.username());
          return new InvalidCredentialsException("Invalid username or password");
        });

    // Verify password
    if (!passwordEncoder.matches(command.password(), user.password())) {
      log.warn("Login failed: invalid password - {}", command.username());
      throw new InvalidCredentialsException("Invalid username or password");
    }

    // Record login timestamp
    User updatedUser = user.recordLogin();
    User savedUser = userRepository.save(updatedUser);

    log.info("User logged in successfully: {}", command.username());

    return userDtoMapper.toDto(savedUser);
  }

  @Transactional(readOnly = true)
  public UserDto getUserByUsername(String username) {
    log.info("Fetching user by username: {}", username);

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> {
          log.warn("Get user failed: user not found - {}", username);
          return UserNotFoundException.withUsername(username);
        });

    return userDtoMapper.toDto(user);
  }
}

