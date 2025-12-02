package sk.cyrilgavala.wardrobeapi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.cyrilgavala.wardrobeapi.exception.DuplicateUserException;
import sk.cyrilgavala.wardrobeapi.exception.UserNotFoundException;
import sk.cyrilgavala.wardrobeapi.model.User;
import sk.cyrilgavala.wardrobeapi.repository.UserRepository;
import sk.cyrilgavala.wardrobeapi.service.SecurityService;
import sk.cyrilgavala.wardrobeapi.service.UserService;
import sk.cyrilgavala.wardrobeapi.web.dto.RegisterRequest;
import sk.cyrilgavala.wardrobeapi.web.dto.UserResponse;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final ModelMapper userMapper;
  private final SecurityService securityService;

  @Override
  public UserResponse getByUsername(String username) {
    log.debug("Fetching user by username: {}", username);
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UserNotFoundException(username));
    return userMapper.map(user, UserResponse.class);
  }

  @Override
  public void saveUser(RegisterRequest request) {
    log.info("Registering new user: {}", request.getUsername());

    // Check if user already exists
    if (userRepository.findByUsername(request.getUsername()).isPresent()) {
      throw new DuplicateUserException("Username already exists: " + request.getUsername());
    }

    // Check if email already exists
    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new DuplicateUserException("Email already exists: " + request.getEmail());
    }

    try {
      User user = userMapper.map(request, User.class);
      user.setPassword(securityService.encryptPassword(request.getPassword()));
      userRepository.save(user);
      log.info("Successfully registered user: {}", request.getUsername());
    } catch (DataIntegrityViolationException e) {
      log.error("Failed to register user due to data integrity violation: {}",
          request.getUsername());
      throw new DuplicateUserException("Email already exists: " + request.getEmail());
    }
  }

}
